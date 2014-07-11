/*
 *  The MIT License
 *
 *  Copyright (c) 2014 Sony Mobile Communications Inc. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.sonymobile.jenkins.plugins.lenientshutdown.cli;

import com.sonymobile.jenkins.plugins.lenientshutdown.Messages;
import com.sonymobile.jenkins.plugins.lenientshutdown.PluginImpl;
import hudson.Extension;
import hudson.cli.CLICommand;
import hudson.model.Computer;
import hudson.model.Node;
import org.kohsuke.args4j.Argument;

/**
 * Sets a node online from being offline leniently or temporary offline.
 * Mimicking the <code>online-node</code> command
 *
 * @see com.sonymobile.jenkins.plugins.lenientshutdown.ShutdownSlaveAction
 * @author &lt;robert.sandell@sonymobile.com&gt;
 */
@Extension
public class LenientOnlineNodeCommand extends CLICommand {

    //CS IGNORE VisibilityModifier FOR NEXT 6 LINES. REASON: How its usually done
    /**
     * The node to act on.
     */
    @Argument(metaVar = "NODE", usage = "Name of the node", required = true)
    public Node node;

    @Override
    public String getShortDescription() {
        return Messages.CancelOfflineLeniently();
    }

    @Override
    protected int run() throws Exception {
        final Computer computer = node.toComputer();
        if (computer != null) {
            computer.checkPermission(Computer.CONNECT);
            PluginImpl plugin = PluginImpl.getInstance();
            if (plugin.isNodeShuttingDown(node.getNodeName())) {
                plugin.toggleNodeShuttingDown(node.getNodeName());
                stdout.println(Messages.NodeTakenOnline(node.getNodeName()));
            } else if (computer.isOffline()) {
                computer.setTemporarilyOffline(false, null);
                stdout.println(Messages.NodeTakenOnline(node.getNodeName()));
            } else {
                stdout.println(Messages.Err_NodeAlreadyOnline(node.getNodeName()));
            }
            return 0;
        } else {
            stderr.println("Not a computer: " + node.getNodeName());
            return -1;
        }
    }
}
