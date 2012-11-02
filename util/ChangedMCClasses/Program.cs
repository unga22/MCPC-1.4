using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace ChangedMCClasses
{
    class Program
    {
        static string[] patchFolders = { @"D:\Git\MCPC-1.4\patches_to_vanilla\Fml", @"D:\Git\MCPC-1.4\patches_to_vanilla\Forge" }; // containc patch files from FML and Forge Github
        static string bukkitMap = @"D:\Git\MCPC-1.4\server-mappings-bukkit.srg"; // replaced net.minecraft.src with net.minecraft.server
        static string mcpMap = @"D:\Git\MCPC-1.4\server-mappings-mcp.srg"; // original


        static string bukkitSource = @"D:\Git\CraftBukkit\src\main\java\net\minecraft\server"; // From Bukkit Github
        static string secondBukkitSource = @"D:\Git\MCP 7 - clean\src\minecraft_server\net\minecraft\server"; // Decompiled forge from 6.0 universal using Bukkit mappings
        static string forgeSource = @"D:\Git\MCP 7 - clean\src\minecraft_server\net\minecraft\server";

        static string workFolder = @"D:\Git\MCPC-1.4\net\minecraft\server"; // Copy bukkit files here which need to be changed (e.g. theres a forge patch for it)
        static string targetPatches = @"D:\Git\MCPC-1.4\patches_to_bukkit"; // Copy patches with bukkit class names here
        static string forgeFilesDest = @"D:\Git\MCPC-1.4\patches_to_bukkit";

        static List<Mapping> mappings = new List<Mapping>();
        static List<Mapping> done_mappings = new List<Mapping>();

        static void Main(string[] args)
        {
            // dont run again. changing manually now
            ListPatchesInBukkit();
            //RemoveComments();

            Console.Read();
        }

        static void ListPatchesInBukkit()
        {
            foreach (string f in Directory.GetFiles(targetPatches))
                File.Delete(f);

            CreateMappings();

            foreach (string patchFolder in patchFolders)
            {
                string[] files = Directory.GetFiles(patchFolder, "*.java.patch");

                foreach (string f in files)
                {
                    string fn = Path.GetFileNameWithoutExtension(Path.GetFileNameWithoutExtension(f));

                    Mapping m = mappings.FirstOrDefault(x => string.Equals("net/minecraft/src/" + fn, x.MCP, StringComparison.InvariantCultureIgnoreCase));
                    if (m == null)
                        m = mappings.FirstOrDefault(x => string.Equals("net/minecraft/server/" + fn, x.MCP, StringComparison.InvariantCultureIgnoreCase));

                    if (m == null)
                        throw new Exception("Unknown class - " + fn + " from " + f);
                    else
                        HandlePatch(f, m);
                }
            }
        }

        static string cleanPacket(string f)
        {
            return f.Substring(f.LastIndexOf('/') + 1);
        }

        static void RemoveComments()
        {
            string dir = @"D:\Git\Bukkit-MCPC-1.4\net\minecraft\server";

            foreach (string f in Directory.GetFiles(dir, "*.java"))
            {
                string[] cont = File.ReadAllLines(f);

                if (cont.Length > 3 && cont[cont.Length - 2].Contains("JD-Core Version:"))
                {
                    List<string> ret = new List<string>();

                    foreach(string l in cont)
                    {
                        string o = l;
                        if (l.Trim().StartsWith("/*") && l.Contains("*/ "))
                            o = l.Substring(l.IndexOf("*/ ") + 3);
                        else if (l.StartsWith("/* Location:") || l.StartsWith(" * JD-Core Version:") || l.StartsWith(" * Qualified Name:") || l == " */")
                            o = null;

                        if (o != null)
                            ret.Add(o);
                    }

                    File.WriteAllLines(f, ret.ToArray());
                }
            }
        }

        static void HandlePatch(string patchFile, Mapping map)
        {
            if (done_mappings.Contains(map))
            {
                Console.WriteLine("- Previously done patch " + map.MCP);
                return;
            }
            done_mappings.Add(map);

            string bukkitSourceFile = Path.Combine(bukkitSource, cleanPacket(map.Bukkit) + ".java");
            string destinationSourceFile = Path.Combine(workFolder, cleanPacket(map.Bukkit) + ".java");

            bool isBukkitChanged = File.Exists(bukkitSourceFile);

            foreach (string patchFolder in patchFolders)
            {
                string[] files = Directory.GetFiles(patchFolder, cleanPacket(map.MCP) + ".java.patch");
                if (files.Length > 0)
                {
                    foreach (string f in files)
                    {
                        int i = 1;
                        string targetPatch = Path.Combine(targetPatches, formatIfForge(cleanPacket(map.Bukkit), isBukkitChanged) + ".java.patch");
                        while (File.Exists(targetPatch))
                            targetPatch = Path.Combine(targetPatches, formatIfForge(cleanPacket(map.Bukkit), isBukkitChanged) + "_" + (++i) + ".java.patch");

                        File.Copy(f, targetPatch);
                    }
                }
            }

            if (!isBukkitChanged)
                bukkitSourceFile = Path.Combine(secondBukkitSource, cleanPacket(map.Bukkit) + ".java");

            string forgeFile = Path.Combine(forgeSource, cleanPacket(map.Bukkit) + ".java");
            File.Copy(forgeFile, Path.Combine(forgeFilesDest, formatIfForge(cleanPacket(map.Bukkit), isBukkitChanged) + ".forge.java"));

            File.Copy(bukkitSourceFile, destinationSourceFile, true);

            Console.WriteLine((isBukkitChanged ? "+ " : "- ") + cleanPacket(map.Bukkit) + (isBukkitChanged ? " Using BUKKIT file" : " using forge, no changes needed"));
        }

        static string formatIfForge(string fn, bool bukkit)
        {
            return (bukkit ? "--" : "") + fn;
        }

        static void CreateMappings()
        {
            string[] bukkit = File.ReadAllLines(bukkitMap);
            string[] mcp = File.ReadAllLines(mcpMap);

            UpdateMappings(bukkit, false);
            UpdateMappings(mcp, true);
        }

        static void UpdateMappings(string[] lines, bool isMcp)
        {
            foreach (string l in lines)
            {
                string[] splits = l.Split(' ');

                if (splits.Length > 2 && splits[0] == "CL:")
                {
                    UpdateMapping(splits[1], splits[2], isMcp);
                }
            }
        }

        static void UpdateMapping(string obf, string val, bool isMcp)
        {
            Mapping map = mappings.FirstOrDefault(x => x.Obfuscated == obf);
            if (map == null)
            {
                map = new Mapping() { Obfuscated = obf };
                mappings.Add(map);
            }

            if (isMcp)
                map.MCP = val;
            else
                map.Bukkit = val;
        }
    }

    class Mapping
    {
        public string Obfuscated;
        public string Bukkit;
        public string MCP;
    }
}
