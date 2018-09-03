package com.deepclas4bioijcomparator;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import java.awt.Choice;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import ij.measure.ResultsTable;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author adines
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>DeepClas4BioIJComparator")
public class DeepClas4BioIJComparator implements Command {

    private String pathAPI;

    @Parameter
    private ImagePlus imp;

    private DefaultListModel<String> listModel;
    private JList list;
    JButton bAddModel;
    JButton bBorrarModel;
    JButton bDeleteAll;

    GenericDialog gd;
    Choice frameworkChoices;
    Choice modelChoices;
    JDialog adAPId = null;

    @Override
    public void run() {
        try {
            String so = System.getProperty("os.name");
            String python;
            if (so.contains("Windows")) {
                python = "python ";
            } else {
                python = "python3 ";
            }

            JFileChooser pathAPIFileChooser = new JFileChooser();
            pathAPIFileChooser.setCurrentDirectory(new java.io.File("."));
            pathAPIFileChooser.setDialogTitle("Select the path of the API");
            pathAPIFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            GridLayout glAPI = new GridLayout(2, 2);
            JPanel apiPanel = new JPanel(glAPI);

            JLabel lPath = new JLabel();
            JButton bPath = new JButton("Select");
            apiPanel.add(new JLabel("Select the path of the API"));
            apiPanel.add(new Label());
            apiPanel.add(lPath);
            apiPanel.add(bPath);

            bPath.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (pathAPIFileChooser.showOpenDialog(apiPanel) == JFileChooser.APPROVE_OPTION) {
                        lPath.setText(pathAPIFileChooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });

            JOptionPane adAPI = new JOptionPane(apiPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION);

            adAPId = adAPI.createDialog("API path");

            adAPId.setVisible(true);
            Object selectedValue = adAPI.getValue();
            if (selectedValue instanceof Integer) {
                int selected = ((Integer) selectedValue).intValue();
                if (selected == 0) {
                    pathAPI = lPath.getText() + File.separator;

                    adAPId.dispose();

                    String comando = python + pathAPI + "listFrameworks.py";
                    Process p = Runtime.getRuntime().exec(comando);
                    p.waitFor();
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("data.json"));
                    JSONArray frameworks = (JSONArray) jsonObject.get("frameworks");

                    imp = IJ.getImage();

                    String path = IJ.getDirectory("image");
                    String name = imp.getTitle();
                    String image = path + name;

                    gd = new GenericDialog("Select Input");
                    gd.setLayout(new GridLayout(2, 3, 5, 5));

                    Panel panel1 = new Panel();
                    frameworkChoices = new Choice();

                    for (Object o : frameworks) {
                        frameworkChoices.addItem((String) o);
                    }
                    frameworkChoices.select("Keras");

                    modelChoices = new Choice();

                    panel1.setLayout(new GridLayout(2, 2));
                    panel1.add(new Label("Framework: "));
                    panel1.add(frameworkChoices);
                    panel1.add(new Label("Model: "));
                    panel1.add(modelChoices);

                    gd.addPanel(panel1);
                    bAddModel = new JButton("Add model");
                    bBorrarModel = new JButton("Delete model");
                    bDeleteAll = new JButton("Delete all models");
                    listModel = new DefaultListModel<String>();
                    list = new JList(listModel);
                    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    list.setLayoutOrientation(JList.VERTICAL);
                    list.setVisibleRowCount(5);
                    list.setPrototypeCellValue("Keras -> GoogLeNetKvasir      ");
                    if (IJ.isLinux()) {
                        list.setBackground(Color.white);
                    }

                    Panel panel = new Panel();
                    panel.setLayout(new GridLayout(3, 1, 5, 5));
                    panel.add(bAddModel);
                    panel.add(bBorrarModel);
                    panel.add(bDeleteAll);

                    Panel panel2 = new Panel();
                    JScrollPane scrollPane2 = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    panel2.add(scrollPane2);

                    gd.addPanel(panel);
                    gd.addPanel(panel2);
                    gd.addPanel(new Panel());

                    bAddModel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            String framework = frameworkChoices.getSelectedItem();
                            String model = modelChoices.getSelectedItem();

                            if (!listModel.contains(framework + " -> " + model)) {
                                listModel.addElement(framework + " -> " + model);
                            }

                        }
                    });

                    bBorrarModel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (!list.isSelectionEmpty()) {
                                listModel.removeElement(list.getSelectedValue());
                            }
                        }
                    });

                    bDeleteAll.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            listModel.clear();
                        }
                    });

                    comando = python + pathAPI + "listModels.py -f Keras";
                    p = Runtime.getRuntime().exec(comando);
                    p.waitFor();
                    JSONParser parser2 = new JSONParser();
                    JSONObject jsonObject2 = (JSONObject) parser2.parse(new FileReader("data.json"));
                    JSONArray models = (JSONArray) jsonObject2.get("models");
                    modelChoices.removeAll();
                    for (Object o : models) {
                        modelChoices.add((String) o);
                    }

                    frameworkChoices.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            try {
                                String frameworkSelected = frameworkChoices.getSelectedItem();
                                String comando = python + pathAPI + "listModels.py -f " + frameworkSelected;
                                Process p = Runtime.getRuntime().exec(comando);
                                p.waitFor();
                                JSONParser parser = new JSONParser();
                                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("data.json"));
                                JSONArray frameworks = (JSONArray) jsonObject.get("models");
                                modelChoices.removeAll();
                                for (Object o : frameworks) {
                                    modelChoices.add((String) o);
                                }
                                modelChoices.doLayout();
                                gd.doLayout();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(DeepClas4BioIJComparator.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(DeepClas4BioIJComparator.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ParseException ex) {
                                Logger.getLogger(DeepClas4BioIJComparator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });

                    gd.showDialog();
                    if (gd.wasCanceled()) {
                        return;
                    }

                    Object[] selectedModels = listModel.toArray();

                    ResultsTable rt = new ResultsTable();

                    for (Object s : selectedModels) {
                        String[] arguments = ((String) s).split(" -> ");
                        comando = python + pathAPI + "predict.py -i " + image + " -f " + arguments[0] + " -m " + arguments[1];
                        System.out.println(comando);
                        p = Runtime.getRuntime().exec(comando);
                        p.waitFor();

                        JSONParser parser3 = new JSONParser();
                        JSONObject jsonObject3 = (JSONObject) parser3.parse(new FileReader("data.json"));
                        String classPredict = (String) jsonObject3.get("class");
                        rt.incrementCounter();
                        rt.addValue("Framework", arguments[0]);
                        rt.addValue("Model", arguments[1]);
                        rt.addValue("Predicted Class", classPredict);

                    }
                    rt.show("Predictions");
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DeepClas4BioIJComparator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(DeepClas4BioIJComparator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DeepClas4BioIJComparator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(DeepClas4BioIJComparator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (adAPId != null) {
                adAPId.dispose();
            }
        }
    }

}
