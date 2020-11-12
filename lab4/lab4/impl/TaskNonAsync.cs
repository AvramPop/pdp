using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using lab4.domain;
using lab4.utils;

namespace lab4.impl
{
    internal static class TaskNonAsync
    {
        private static List<string> _hostNames;

        public static void Run(List<string> hosts)
        {
            _hostNames = hosts;
            var tasks = new List<Task>();
            for (var i = 0; i < hosts.Count; i++)
            {
                tasks.Add(Task.Factory.StartNew(DoStart, i));
            }
            Task.WaitAll(tasks.ToArray());
        }

        private static void DoStart(object idObject)
        {
            var id = (int) idObject;
            StartClient(_hostNames[id], id);
        }

        private static void StartClient(string host, int id)
        {
            var ipHostInfo = Dns.GetHostEntry(host.Split('/')[0]);
            var ipAddress = ipHostInfo.AddressList[0];
            var remoteEndPoint = new IPEndPoint(ipAddress, Utils.PORT);
            var socket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            var state = new SocketWrapper
            {
                socket = socket,
                hostname = host.Split('/')[0],
                endpoint = host.Contains("/") ? host.Substring(host.IndexOf("/", StringComparison.Ordinal)) : "/",
                remoteEndPoint = remoteEndPoint,
                id = id
            };

            ConnectWrapper(state).Wait();
            string requestString = Utils.GetRequestString(state.hostname, state.endpoint);
            SendWrapper(state, requestString).Wait();
            ReceiveWrapper(state).Wait();
            Console.WriteLine("#{0}: Content length is: {1}", id, Utils.GetContentLength(state.responseContent.ToString()));
            socket.Shutdown(SocketShutdown.Both);
            socket.Close();
        }

        private static Task ConnectWrapper(SocketWrapper state)
        {
            state.socket.BeginConnect(state.remoteEndPoint, WhenConnected, state);
            return Task.FromResult(state.connectDone.WaitOne());
        }

        private static void WhenConnected(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper) asyncResult.AsyncState;
            var clientSocket = state.socket;
            var clientId = state.id;
            clientSocket.EndConnect(asyncResult);
            Console.WriteLine("#{0}: Socket connected to {1}", clientId, clientSocket.RemoteEndPoint);
            state.connectDone.Set();
        }

        private static Task SendWrapper(SocketWrapper state, string data)
        {
            var requestAsBytes = Encoding.ASCII.GetBytes(data);
            state.socket.BeginSend(requestAsBytes, 0, requestAsBytes.Length, 0, WhenSent, state);
            return Task.FromResult(state.sendDone.WaitOne());
        }

        private static void WhenSent(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper) asyncResult.AsyncState;
            var clientSocket = state.socket;
            var clientId = state.id;

            var bytesSent = clientSocket.EndSend(asyncResult);
            Console.WriteLine("#{0}: Sent {1} bytes to server.", clientId, bytesSent);
            state.sendDone.Set();
        }

        private static Task ReceiveWrapper(SocketWrapper state)
        {
            state.socket.BeginReceive(state.buffer, 0, 512, 0, WhenReceived, state);
            return Task.FromResult(state.receiveDone.WaitOne());
        }

        private static void WhenReceived(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper) asyncResult.AsyncState;
            var clientSocket = state.socket;

            try
            {
                var bytesRead = clientSocket.EndReceive(asyncResult);
                state.responseContent.Append(Encoding.ASCII.GetString(state.buffer, 0, bytesRead));
                if (!Utils.ResponseHeaderObtained(state.responseContent.ToString()))
                {
                    clientSocket.BeginReceive(state.buffer, 0, 512, 0, WhenReceived, state);
                }
                else
                {
                    // Utils.PrintResponse(state);
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