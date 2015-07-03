# ccw-plugin-manager

This project is a Counterclockwise User Plugin providing facilities to manage User Plugins. 

This plugin's state is stable.

## Install

The `~/.ccw/` folder is where Counterclockwise searches for User Plugins.

It is recommended to layout User Plugins inside this folder by mirroring Github's namespacing. So if you clone ccw-ide/ccw-plugin-manager, you should do the following:

- Create a folder named `~/.ccw/ccw-ide/`
- Clone this project from `~/.ccw/ccw-ide/`

        mkdir -p ~/.ccw/ccw-ide
        cd ~/.ccw/ccw-ide
        git clone https://github.com/ccw-ide/ccw-plugin-manager.git

- Restart your Eclipse / Counterclockwise/Standalone instance.

## Usage

This User Plugin installs a few keybindings to help work with other User Plugins.

- `Alt+U S` : re[S]tart user plugins
- `Alt+U N` : create [N]ew user plugin
- `Alt+U I` : [I]mport all user plugins as projects in workspace

## License

Copyright © 2009-2015 Laurent Petit

Distributed under the Eclipse Public License, the same as Clojure.

