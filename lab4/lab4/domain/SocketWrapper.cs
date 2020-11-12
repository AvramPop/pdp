using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace lab4.domain
{
    internal class SocketWrapper
    {
        public Socket socket = null;
        public readonly ManualResetEvent sendDone = new ManualResetEvent(false);
        public readonly ManualResetEvent connectDone = new ManualResetEvent(false);
        public readonly ManualResetEvent receiveDone = new ManualResetEvent(false);
        public string endpoint;
        public string hostname;
        public int id;
        public IPEndPoint remoteEndPoint;
        public StringBuilder responseContent = new StringBuilder();
        public byte[] buffer = new byte[512];
    }
}