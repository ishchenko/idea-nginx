/*
 * Copyright 2009 Max Ishchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ishchenko.idea.nginx.configurator;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import java.awt.*;
import javax.swing.*;
import net.ishchenko.idea.nginx.NginxBundle;
import net.ishchenko.idea.nginx.platform.PlatformDependentTools;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 27.07.2009
 * Time: 17:55:06
 */
public class NginxConfigurationPanel {

    private NginxServersConfiguration config;

    private JSplitPane panel;
    private JList serverList;

    private TrickyMediator mediator = new TrickyMediator();

    public NginxConfigurationPanel(NginxServersConfiguration config) {

        this.config = config;

        JPanel leftComponent = new JPanel(new BorderLayout());
        JPanel rightComponent = new ServerFieldsForm(mediator).getPanel();

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(createAddButtom());
        buttonsPanel.add(createRemoveButton());

        serverList = createServerList();
        mediator.serverList = serverList;

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(serverList);

        leftComponent.add(buttonsPanel, BorderLayout.NORTH);
        leftComponent.add(scrollPane, BorderLayout.CENTER);

        panel = new JSplitPane();
        panel.setLeftComponent(leftComponent);
        panel.setRightComponent(rightComponent);
        panel.setDividerLocation(300);

    }

    private JList createServerList() {
        JList result = new JList();
        result.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component rendered = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(((NginxServerDescriptor) value).getName());
                setIcon(IconLoader.getIcon("/nginx.png"));
                return rendered;
            }
        });
        result.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        result.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                final NginxServerDescriptor selectedDescriptor = (NginxServerDescriptor) serverList.getSelectedValue();
                if (selectedDescriptor != null) {
                    SwingUtilities.invokeLater(() -> mediator.showDescriptor(selectedDescriptor));
                }
            }
        });
        return result;
    }

    private JButton createAddButtom() {
        JButton button = new JButton(NginxBundle.message("run.newserver"));
        button.addActionListener(e -> SwingUtilities.invokeLater(() -> mediator.addNewServerClicked()));
        button.setMnemonic('n');
        mediator.addButton = button;
        return button;
    }

    private JButton createRemoveButton() {
        JButton button = new JButton(NginxBundle.message("run.removeserver"));
        button.addActionListener(e -> {
            final NginxServerDescriptor selectedDescriptor = (NginxServerDescriptor) serverList.getSelectedValue();
            if (selectedDescriptor != null) {
                SwingUtilities.invokeLater(() -> mediator.removeDescriptor(selectedDescriptor));
            }
        });
        mediator.removeButton = button;
        button.setMnemonic('r');
        return button;
    }

    public synchronized void reset() {
        DefaultListModel model = new DefaultListModel();
        for (NginxServerDescriptor descriptor : this.config.getServersDescriptors()) {
            model.addElement(descriptor.clone());
        }
        serverList.setModel(model);
    }

    public synchronized void apply() throws ConfigurationException {

        PlatformDependentTools pdt = ServiceManager.getService(PlatformDependentTools.class);
        for (int i = 0; i < serverList.getModel().getSize(); i++) {
            NginxServerDescriptor descriptor = (NginxServerDescriptor) serverList.getModel().getElementAt(i);
            if (!pdt.checkExecutable(descriptor.getExecutablePath())) {
                serverList.setSelectedIndex(i);
                throw new ConfigurationException(NginxBundle.message("run.error.badpath"));
            }
        }

        config.removeAllServerDescriptors();
        for (int i = 0; i < serverList.getModel().getSize(); i++) {
            NginxServerDescriptor descriptor = (NginxServerDescriptor) serverList.getModel().getElementAt(i);
            config.addServerDescriptor(descriptor.clone());
        }
    }

    public JComponent getPanel() {
        return panel;
    }

    public boolean isModified() {

        if (serverList.getModel().getSize() != config.getServersDescriptors().length) {
            return true;
        }

        for (int i = 0; i < serverList.getModel().getSize(); i++) {
            NginxServerDescriptor descriptor = (NginxServerDescriptor) serverList.getModel().getElementAt(i);
            if (!descriptor.equals(config.getServersDescriptors()[i])) {
                return true;
            }
        }

        return false;
    }

    static class TrickyMediator {

        JList serverList;
        JButton addButton;
        JButton removeButton;
        JTextField nameField;
        JTextField executableField;
        JTextField configField;
        JTextField pidField;
        JTextField globalsField;

        PlatformDependentTools pdt;

        public TrickyMediator() {
            pdt = ServiceManager.getService(PlatformDependentTools.class);
        }

        public void addNewServerClicked() {

            VirtualFile[] file = FileChooser.chooseFiles(new NginxExecutableFileChooserDescriptor(), serverList, null, null);
            if (file.length > 0) {

                NginxServerDescriptor newDescriptor = getDescriptorFromFile(file[0]);

                if (newDescriptor != null) {
                    newDescriptor.setName(getUniqueName(newDescriptor.getName()));
                    DefaultListModel model = (DefaultListModel) serverList.getModel();
                    model.addElement(newDescriptor);
                    serverList.setSelectedIndex(model.getSize() - 1);
                }

            }
        }

        public void removeDescriptor(NginxServerDescriptor descriptor) {
            ((DefaultListModel) serverList.getModel()).removeElement(descriptor);
            nameField.setText("");
            executableField.setText("");
            configField.setText("");
            pidField.setText("");
            globalsField.setText("");
            removeButton.setEnabled(false);
        }

        public void showDescriptor(NginxServerDescriptor descriptor) {
            nameField.setText(descriptor.getName());
            executableField.setText(descriptor.getExecutablePath());
            configField.setText(descriptor.getConfigPath());
            pidField.setText(descriptor.getPidPath());
            globalsField.setText(descriptor.getGlobals());
            removeButton.setEnabled(true);
        }

        public void chooseExecutableClicked() {

            VirtualFile oldFile = LocalFileSystem.getInstance().findFileByPath(executableField.getText());
            VirtualFile[] chosen = FileChooser.chooseFiles(new NginxExecutableFileChooserDescriptor(), serverList, null, oldFile);

            if (chosen.length > 0) {

                NginxServerDescriptor descriptor = getDescriptorFromFile(chosen[0]);

                if (descriptor != null) {
                    executableField.setText(descriptor.getExecutablePath());
                    configField.setText(descriptor.getConfigPath());
                    pidField.setText(descriptor.getPidPath());
                    sync();
                }
            }

        }

        private NginxServerDescriptor getDescriptorFromFile(VirtualFile chosen) {

            boolean useDefaultDescriptor = false;
            NginxServerDescriptor descriptor = null;

            if (!userAgreesToRunExecutable()) {
                useDefaultDescriptor = true;
            } else {
                try {
                    descriptor = pdt.createDescriptorFromFile(chosen);
                } catch (PlatformDependentTools.ThisIsNotNginxExecutableException e) {
                    useDefaultDescriptor = userAgreesThatItIsNotNginx();
                }
            }

            if (useDefaultDescriptor) {
                descriptor = pdt.getDefaultDescriptorFromFile(chosen);
            }
            return descriptor;
        }

        public void chooseConfigurationClicked() {
            VirtualFile oldFile = LocalFileSystem.getInstance().findFileByPath(configField.getText());
            VirtualFile[] file = FileChooser.chooseFiles(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), serverList, null, oldFile);
            if (file.length > 0) {
                configField.setText(file[0].getPath());
                sync();
            }
        }

        public void choosePidClicked() {
            VirtualFile oldFile = LocalFileSystem.getInstance().findFileByPath(pidField.getText());
            VirtualFile[] file = FileChooser.chooseFiles(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), serverList, null, oldFile);
            if (file.length > 0) {
                pidField.setText(file[0].getPath());
                sync();
            }
        }

        public void sync() {
            if (serverList.getSelectedValue() != null) {
                NginxServerDescriptor descriptor = (NginxServerDescriptor) serverList.getSelectedValue();
                descriptor.setName(nameField.getText());
                descriptor.setExecutablePath(executableField.getText());
                descriptor.setConfigPath(configField.getText());
                descriptor.setPidPath(pidField.getText());
                descriptor.setGlobals(globalsField.getText());
                serverList.updateUI();
            }
        }

        private boolean userAgreesThatItIsNotNginx() {
            final DialogBuilder builder = new DialogBuilder(serverList);

            JLabel label = new JLabel(NginxBundle.message("run.notnginx"), IconLoader.getIcon("/notnginx.png"), SwingConstants.LEFT);
            label.setUI(new MultiLineLabelUI());

            builder.setTitle(NginxBundle.message("run.notnginx.warning"));
            builder.setCenterPanel(label);

            return builder.show() == DialogWrapper.OK_EXIT_CODE;
        }

        private boolean userAgreesToRunExecutable() {

            final DialogBuilder builder = new DialogBuilder(serverList);

            JLabel label = new JLabel(NginxBundle.message("run.doyouwanttorun"));
            builder.setTitle(NginxBundle.message("run.notnginx.warning"));
            builder.setCenterPanel(label);

            return builder.show() == DialogWrapper.OK_EXIT_CODE;

        }

        private String getUniqueName(String name) {
            while (notUnique(name)) {
                name = name + "_";
            }
            return name;
        }

        private boolean notUnique(String name) {
            for (int i = 0; i < serverList.getModel().getSize(); i++) {
                NginxServerDescriptor descriptor = (NginxServerDescriptor) serverList.getModel().getElementAt(i);
                if (descriptor.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }
}
