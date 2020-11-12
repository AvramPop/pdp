using System.Linq;
using lab4.impl;

namespace lab4
{
    internal static class Lab4
    {
        private static void Main()
        {
            var hosts = new[]
                {
                    "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/lab-4-futures-continuations.html", 
                    "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/lab-5-parallel-algo.html",
                    "www.cs.ubbcluj.ro/~rlupsa/edu/index.html"
                }
                .ToList();
            // TaskNonAsync.Run(hosts);
            TaskAsync.Run(hosts);
            // Callback.Run(hosts);
        }
    }
}