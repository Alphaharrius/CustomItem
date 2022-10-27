package test;

import dev.customitem.command.CommandLoader;

public class Test {

    public static void main(String[] args) {

        CommandLoader commandLoader = new CommandLoader();
        commandLoader.loadCommandExecutors();
    }

}
