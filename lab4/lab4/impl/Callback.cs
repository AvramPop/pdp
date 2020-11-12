using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using lab4.domain;
using lab4.utils;

namespace lab4.impl
{
    internal static class Callback
    {
        private static int _currentIndex;

        public static void Run(List<string> hosts)
        {
            hosts.ForEach(DoStart);
        }

        private static void DoStart(string hostname)
        {
            StartClient(hostname);
            Thread.Sleep(2000);
        }

        private static void StartClient(string host)
        {
            var ipHostInfo = Dns.GetHostEntry(host.Split('/')[0]);
            var ipAddress = ipHostInfo.AddressList[0];
            var remoteEndpoint = new IPEndPoint(ipAddress, Utils.PORT);
            var state = new SocketWrapper
            {
                socket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp),
                hostname = host.Split('/')[0],
                endpoint = host.Contains("/") ? host.Substring(host.IndexOf("/", StringComparison.Ordinal)) : "/",
                remoteEndPoint = remoteEndpoint,
                id = _currentIndex
            };
            _currentIndex++;
            state.socket.BeginConnect(state.remoteEndPoint, WhenConnected, state);
        }

        private static void WhenConnected(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper) asyncResult.AsyncState;
            var clientSocket = state.socket;
            var clientId = state.id;
            clientSocket.EndConnect(asyncResult);
            Console.WriteLine("#{0}: Socket connected to {1}", clientId, clientSocket.RemoteEndPoint);
            var requestString = Utils.GetRequestString(state.hostname, state.endpoint);
            var requestAsBytes = Encoding.ASCII.GetBytes(requestString);
            state.socket.BeginSend(requestAsBytes, 0, requestAsBytes.Length, 0, WhenSent, state);
        }

        private static void WhenSent(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper) asyncResult.AsyncState;
            var clientSocket = state.socket;
            var clientId = state.id;
            var bytesSent = clientSocket.EndSend(asyncResult);
            Console.WriteLine("#{0}: Sent {1} bytes to server.", clientId, bytesSent);
            state.socket.BeginReceive(state.buffer, 0, 512, 0, WhenReceiving, state);
        }

        private static void WhenReceiving(IAsyncResult asyncResult)
        {
            var state = (SocketWrapper) asyncResult.AsyncState;
            var clientSocket = state.socket;
            try
            {
                var bytesRead = clientSocket.EndReceive(asyncResult);
                state.responseContent.Append(Encoding.ASCII.GetString(state.buffer, 0, bytesRead));
                if (!Utils.ResponseHeaderObtained(state.responseContent.ToString()))
                {
                    clientSocket.BeginReceive(state.buffer, 0, 512, 0, WhenReceiving, state);
                }
                else
                {
                    var responseBody =
                        Utils.GetResponseBody(state.responseContent
                            .ToString());
                    var contentLengthHeaderValue = Utils.GetContentLength(state.responseContent.ToString());
                    if (responseBody.Length < contentLengthHeaderValue)
                    {
                        clientSocket.BeginReceive(state.buffer, 0, 512, 0, WhenReceiving, state);
                    }
                    else
                    {
                        // Utils.PrintResponse(state);
                        Console.WriteLine("Content length is: {0}",
                            Utils.GetContentLength(state.responseContent.ToString()));
                        clientSocket.Shutdown(SocketShutdown.Both);
                        clientSocket.Close();
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }
        
    }
}