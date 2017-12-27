package com.univ.labs.shell;

import com.univ.labs.fileSystem.AMDFileSystem;
import com.univ.labs.fileSystem.errors.OutOfMemory;
import com.univ.labs.fileSystem.errors.ShellArgumentsException;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Masha Kereb on 24-Apr-17.
 */

public class Shell implements ShellInterface {
    private AMDFileSystem fileSystem;
    private boolean stop = false;


    private static Shell ourInstance = new Shell();

    public static Shell getInstance() {
        return ourInstance;
    }

    private Shell() {
        try {
            this.fileSystem = new AMDFileSystem();
        } catch (OutOfMemory e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void processCommand(String commandString) throws Exception {
        String[] args = commandString.split(" ");

        if (args.length == 0) {
            return;
        }
        String command = args[0];
        switch (command) {

            case "ls":
                if (args.length > 1)
                    throw new ShellArgumentsException(args[1]);
                for (String name : this.fileSystem.directory()
                        ) {
                    System.out.println(name);
                }
                break;

            case "help":
                if (args.length > 1)
                    throw new ShellArgumentsException(args[1]);
                this.help();
                break;

            case "cr":
                if (args.length == 1)
                    throw new ShellArgumentsException("<filename> argument expected");
                for (int i = 1; i < args.length; i++)
                    this.fileSystem.create(args[i]);
                break;

            case "op":
                if (args.length == 1)
                    throw new ShellArgumentsException("<filename> argument expected");
                for (int i = 1; i < args.length; i++)
                    System.out.println(this.fileSystem.open(args[i]) + " -- " + args[i]);
                break;

            case "cl":
                if (args.length == 1)
                    throw new ShellArgumentsException("<index> argument expected");
                for (int i = 1; i < args.length; i++)
                    this.fileSystem.close(Integer.valueOf(args[i]));
                break;

            case "q":
                if (args.length > 1)
                    throw new ShellArgumentsException(args[1]);
                this.quit();
                break;

            case "del":
                if (args.length == 1)
                    throw new ShellArgumentsException("<filename> argument expected");
                for (int i = 1; i < args.length; i++)
                    this.fileSystem.destroy(args[i]);
                break;

            case "rd":
                if (args.length < 3)
                    throw new ShellArgumentsException("<index> and <count> arguments expected");
                if (args.length > 3)
                    throw new ShellArgumentsException(args[3]);
                byte[] res = this.fileSystem.read(Integer.valueOf(args[1]), Integer.valueOf(args[2]));
                System.out.println(new String(res, "UTF-8"));
                break;

            case "wt":
                if (args.length < 3)
                    throw new ShellArgumentsException("<index> and <count> arguments expected");

                StringBuilder sb = new StringBuilder(args[2]);
                for (int i = 3; i < args.length; i++) {
                    sb.append(" ");
                    sb.append(args[i]);
                }

                this.fileSystem.write(Integer.valueOf(args[1]), sb.toString().getBytes("UTF-8"));
                break;

            case "lsk":
                if (args.length < 3)
                    throw new ShellArgumentsException("<index> and <pos> arguments expected");
                if (args.length > 3)
                    throw new ShellArgumentsException(args[3]);
                this.fileSystem.lseek(Integer.valueOf(args[1]), Integer.valueOf(args[2]));
                break;

            case "dump":
                if (args.length < 2)
                    throw new ShellArgumentsException("<filename> argument expected");
                this.fileSystem.dump(args[1]);
                break;

            case "load":
                if (args.length < 2)
                    throw new ShellArgumentsException("<filename> argument expected");
                this.fileSystem.load(args[1]);
                break;

            default:
                throw new ShellArgumentsException("Unknown command");
        }
    }

    @Override
    public void start() {
        String greeting = "Welcome to AMD File System! \n";
        Scanner scanner = new Scanner(System.in);
        System.out.print(greeting);
        String com;
        do {
            System.out.print(">");
            com = scanner.nextLine();
            try {
                this.processCommand(com);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (!this.stop);

    }

    @Override
    public void help() {
        String helpString = "commands and arguments: \n" +
                "op <name>          : opens the named file for reading and writing," +
                "                     and displays an index value on the screen \n" +
                "lsk <index> <pos>  : moves current cursor position in <index> file to specified as <pos>" +
                "rd <index> <count> : reads the number of bytes specified as <count>" +
                "                     from the open file <index> and displays them on the screen\n" +
                "cr <name>          : creates a new file with the specified name\n" +
                "del <name>         : deletes a file with the specified name\n" +
                "ls                 : displays the list of files in the current directory\n" +
                "dump <name>        : save the filesystem to specified file <name>\n" +
                "load <name>        : load filesystem from specified file <name>\n" +
                "help               : displays the listing of available commands\n" +
                "cl <index>         : close file with specified index\n" +
                "wt <index> <info>  : write to specified file\n" +
                "q                  : close session and exit\n";

        System.out.print(helpString);

    }

    @Override
    public void quit() {
        this.stop = true;

    }

    public void readFromFile(java.io.File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);
        String command;
        while (scanner.hasNext()) {
            try {
                command = scanner.nextLine();
                System.out.println(command);
                this.processCommand(command);

            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
            System.out.println("---------------------");
        }
    }


    public static void main(String[] args) {
        Shell shell = Shell.getInstance();
         shell.start();
//        String command;
//        try {
//            shell.readFromFile(new java.io.File("commands.txt"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }


}
