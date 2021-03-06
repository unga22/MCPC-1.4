package cpw.mods.fml.common.toposort;

import cpw.mods.fml.common.toposort.TopologicalSort$DirectedGraph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TopologicalSort
{
    public static List topologicalSort(TopologicalSort$DirectedGraph var0)
    {
        TopologicalSort$DirectedGraph var1 = reverse(var0);
        ArrayList var2 = new ArrayList();
        HashSet var3 = new HashSet();
        HashSet var4 = new HashSet();
        Iterator var5 = var1.iterator();

        while (var5.hasNext())
        {
            Object var6 = var5.next();
            explore(var6, var1, var2, var3, var4);
        }

        return var2;
    }

    public static TopologicalSort$DirectedGraph reverse(TopologicalSort$DirectedGraph var0)
    {
        TopologicalSort$DirectedGraph var1 = new TopologicalSort$DirectedGraph();
        Iterator var2 = var0.iterator();
        Object var3;

        while (var2.hasNext())
        {
            var3 = var2.next();
            var1.addNode(var3);
        }

        var2 = var0.iterator();

        while (var2.hasNext())
        {
            var3 = var2.next();
            Iterator var4 = var0.edgesFrom(var3).iterator();

            while (var4.hasNext())
            {
                Object var5 = var4.next();
                var1.addEdge(var5, var3);
            }
        }

        return var1;
    }

    public static void explore(Object var0, TopologicalSort$DirectedGraph var1, List var2, Set var3, Set var4)
    {
        if (var3.contains(var0))
        {
            if (!var4.contains(var0))
            {
                System.out.printf("%s: %s\n%s\n%s\n", new Object[] {var0, var2, var3, var4});
                throw new ModSortingException("There was a cycle detected in the input graph, sorting is not possible", var0, var3);
            }
        }
        else
        {
            var3.add(var0);
            Iterator var5 = var1.edgesFrom(var0).iterator();

            while (var5.hasNext())
            {
                Object var6 = var5.next();
                explore(var6, var1, var2, var3, var4);
            }

            var2.add(var0);
            var4.add(var0);
        }
    }
}
