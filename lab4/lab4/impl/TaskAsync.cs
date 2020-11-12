using lab4.domain;
using lab4.utils;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace lab4.impl
{
    static class TaskAsync
    {
        private static List<string> _hostNames;

        public static void Run(List<string> hostnames)
        {
            _hostNames = hostnames;
            var tasks = new List<Task>();
            for (var i = 0; i < hostnames.Count; i++)
            {
                tasks.Add(Task.Factory.StartNew(DoStartAsync, i));
            }
            Task.WaitAll(tasks.ToArray());
        }

        private static void DoStartAsync(object idObject)
        {
            var id = (int) idObject;
            StartAsyncClient(_hostNames[id], id);
        }

        private static async void StartAsyncClient(string host, int id)
        {
            var ipHostInfo = Dns.GetHostEntry(host.Split('/')[0]);
            var ipAddress = ipHostInfo.AddressList[0];
            var remoteEndpoint = new IPEndPoint(ipAddress, Utils.PORT);
            var client =
                new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            var state = new SocketWrapper
            {
                socket = client,
                hostname = host.Split('/')[0],
                endpoint = host.Contains("/") ? host.Substring(host.IndexOf("/", StringComparison.Ordinal)) : "/",
                remoteEndPoint = remoteEndpoint,
                id = id
            };
            await ConnectAsyncWrapper(state);
            string requestString = Utils.GetRequestString(state.hostname, state.endpoint);
            await SendAsyncWrapper(state, requestString);
            await ReceiveAsyncWrapper(state);
            Console.WriteLine("#{0}: Content length is:{1}", id, Utils.GetContentLength(state.responseContent.ToString()));
            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }

        private static async Task ConnectAsyncWrapper(SocketWrapper state)
        {
            state.socket.BeginConnect(state.remoteEndPoint, ConnectCallback, state);
            await Task.FromResult<object>(state.connectDone.WaitOne());
        }
        
        private static void ConnectCallback(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper)asyncResult.AsyncState;
            var clientSocket = state.socket;
            var clientId = state.id;
            clientSocket.EndConnect(asyncResult);
            Console.WriteLine("#{0}: Socket connected to {1}", clientId, clientSocket.RemoteEndPoint);
            state.connectDone.Set();
        }

        private static async Task SendAsyncWrapper(SocketWrapper state, string data)
        {
            var requestBytes = Encoding.ASCII.GetBytes(data);
            state.socket.BeginSend(requestBytes, 0, requestBytes.Length, 0, WhenSent, state);
            await Task.FromResult<object>(state.sendDone.WaitOne());
        }
        
        private static void WhenSent(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper)asyncResult.AsyncState;
            var clientSocket = state.socket;
            var clientId = state.id;
            var bytesSent = clientSocket.EndSend(asyncResult);
            Console.WriteLine("#{0}: Sent {1} bytes to server.", clientId, bytesSent);
            state.sendDone.Set();
        }

        private static async Task ReceiveAsyncWrapper(SocketWrapper state)
        {
            state.socket.BeginReceive(state.buffer, 0, 512, 0, ReceiveCallback, state);
            await Task.FromResult<object>(state.receiveDone.WaitOne());
        }
        
        private static void ReceiveCallback(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper) asyncResult.AsyncState;
            var clientSocket = state.socket;
            try
            {
                var bytesRead = clientSocket.EndReceive(asyncResult);
                state.responseContent.Append(Encoding.ASCII.GetString(state.buffer, 0, bytesRead));
                if (!Utils.ResponseHeaderObtained(state.responseContent.ToString()))
                {
                    clientSocket.BeginReceive(state.buffer, 0, 512, 0, ReceiveCallback, state);
                }
                else
                {
                    Utils.PrintResponse(state);
                    state.receiveDone.Set();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }
    }
}