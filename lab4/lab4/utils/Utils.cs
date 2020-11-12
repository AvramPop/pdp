using System;
using lab4.domain;

namespace lab4.utils
{
    internal class Utils
    {
        public static readonly int PORT = 80;
        // http request and response format
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages
        public static string GetResponseBody(string responseContent)
        {
            var result = responseContent.Split(new[] {"\r\n\r\n"}, StringSplitOptions.RemoveEmptyEntries);
            if (result.Length > 1) return result[1];
            return "";
        }

        public static string GetRequestString(string hostname, string endpoint)
        {
            return "GET " + endpoint + " HTTP/1.1\r\n" +
                   "Host: " + hostname + "\r\n" +
                   "Content-Length: 0\r\n\r\n";
        }


        public static int GetContentLength(string respContent)
        {
            var contentLen = 0;
            var respLines = respContent.Split('\r', '\n');
            foreach (var respLine in respLines)
            {
                var headDetails = respLine.Split(':');
                if (string.Compare(headDetails[0], "Content-Length", StringComparison.Ordinal) == 0)
                    contentLen = int.Parse(headDetails[1]);
            }
            return contentLen;
        }

        public static bool ResponseHeaderObtained(string responseContent)
        {
            return responseContent.Contains("\r\n\r\n");
        }
        
        public static void PrintResponse(SocketWrapper state)
        {
            foreach (var i in state.responseContent.ToString().Split('\r', '\n'))
                Console.WriteLine(i);
        }
    }
}