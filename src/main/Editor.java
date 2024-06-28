package main;

import entities.Camera;
import entities.Entity;
import entities.Light;

import javax.swing.*;
import java.util.List;

public class Editor extends JFrame {

    private Entity selectedEntity;
    private Light selectedLight;

    private JTabbedPane editorTabbedPane;
    private JComboBox<Entity> entityComboBox;
    private JSlider xRotationSlider;
    private JSpinner xRotationSpinner;
    private JPanel entityPropertyContainer;
    private JPanel zPositionContainer;
    private JPanel yPositionContainer;
    private JPanel xPositionContainer;
    private JPanel xRotationContainer;
    private JPanel yRotationContainer;
    private JPanel zRotationContainer;
    private JPanel scaleContainer;
    private JSpinner zPositionSpinner;
    private JSpinner yPositionSpinner;
    private JSpinner xPositionSpinner;
    private JSpinner yRotationSpinner;
    private JSpinner zRotationSpinner;
    private JSlider zRotationSlider;
    private JSlider yRotationSlider;
    private JSlider scaleSlider;
    private JSpinner scaleSpinner;
    private JPanel editorContainer;
    private JPanel entityTab;
    private JPanel lightTab;
    private JPanel cameraTab;
    private JScrollPane entityScrollPane;
    private JPanel entityWrapper;
    private JScrollPane lightScrollPane;
    private JPanel lightWrapper;
    private JComboBox<Light> lightComboBox;
    private JPanel lightPropertyContainer;
    private JCheckBox useFakeLightingCheckBox;
    private JPanel useFakeLightingContainer;
    private JTextField entityNameTextField;
    private JButton updateEntityNameButton;
    private JPanel entityNameContainer;


    public Editor(List<Entity> entities, List<Light> lights, Camera camera) {
        initializeComboBoxes(entities, lights);
        initializeSlidersAndSpinners();
        initializeOtherComponents();

        setContentPane(editorContainer);
        setMinimumSize(editorContainer.getMinimumSize());
        setTitle("World Editor");
        setVisible(true);
        setAlwaysOnTop(true);

    }


    private void initializeComboBoxes(List<Entity> entities, List<Light> lights) {
        DefaultComboBoxModel<Entity> entityModel = new DefaultComboBoxModel<>(entities.toArray(new Entity[0]));
        entityComboBox.setModel(entityModel);
        entityComboBox.addActionListener(e -> {
            selectedEntity = (Entity) entityComboBox.getSelectedItem();
            updateEditorEntityProperties();
        });
        selectedEntity = (Entity) entityComboBox.getSelectedItem();
        updateEditorEntityProperties();

        DefaultComboBoxModel<Light> lightModel = new DefaultComboBoxModel<>(lights.toArray(new Light[0]));
        lightComboBox.setModel(lightModel);
        lightComboBox.addActionListener(e -> {
            selectedLight = (Light) lightComboBox.getSelectedItem();
            updateEditorLightProperties();
        });
        selectedLight = (Light) lightComboBox.getSelectedItem();
        updateEditorLightProperties();
    }


    private void initializeSlidersAndSpinners() {
        // position
        xPositionSpinner.addChangeListener(e -> selectedEntity.getPosition().x = Float.parseFloat(String.valueOf(xPositionSpinner.getModel().getValue())));
        yPositionSpinner.addChangeListener(e -> selectedEntity.getPosition().y = Float.parseFloat(String.valueOf(yPositionSpinner.getModel().getValue())));
        zPositionSpinner.addChangeListener(e -> selectedEntity.getPosition().z = Float.parseFloat(String.valueOf(zPositionSpinner.getModel().getValue())));

        // rotation
        xRotationSpinner.addChangeListener(e -> {
            float value = Float.parseFloat(String.valueOf(xRotationSpinner.getModel().getValue()));
            value = normalizeRotationValue(value);
            xRotationSpinner.getModel().setValue(value);
            xRotationSlider.setValue((int) value);
            selectedEntity.setRotX(value);
        });
        yRotationSpinner.addChangeListener(e -> {
            float value = Float.parseFloat(String.valueOf(yRotationSpinner.getModel().getValue()));
            value = normalizeRotationValue(value);
            yRotationSpinner.getModel().setValue(value);
            yRotationSlider.setValue((int) value);
            selectedEntity.setRotY(value);
        });
        zRotationSpinner.addChangeListener(e -> {
            float value = Float.parseFloat(String.valueOf(zRotationSpinner.getModel().getValue()));
            value = normalizeRotationValue(value);
            zRotationSpinner.getModel().setValue(value);
            zRotationSlider.setValue((int) value);
            selectedEntity.setRotZ(value);
        });
        xRotationSlider.addChangeListener(e -> {
            float value = (float) xRotationSlider.getModel().getValue();
            xRotationSpinner.getModel().setValue(value);
            selectedEntity.setRotX(value);
        });
        yRotationSlider.addChangeListener(e -> {
            float value = (float) yRotationSlider.getModel().getValue();
            yRotationSpinner.getModel().setValue(value);
            selectedEntity.setRotY(value);
        });
        zRotationSlider.addChangeListener(e -> {
            float value = (float) zRotationSlider.getModel().getValue();
            zRotationSpinner.getModel().setValue(value);
            selectedEntity.setRotZ(value);
        });

        // scale
        scaleSpinner.addChangeListener(e -> {
            float value = Float.parseFloat(String.valueOf(scaleSpinner.getModel().getValue()));
            if (value < 0) {
                value = 0.1f;
            }
            if (value > 10) {
                value = 10;
            }
            scaleSpinner.getModel().setValue(value);
            int sliderValue = (int) (value * 10);
            scaleSlider.setValue(sliderValue);
            selectedEntity.setScale(value);
        });
        scaleSlider.addChangeListener(e -> {
            int sliderValue = scaleSlider.getModel().getValue();
            float value = sliderValue / 10.0f;
            scaleSpinner.getModel().setValue(value);
            selectedEntity.setScale(value);
        });

    }
    private float normalizeRotationValue(float value) {
        value = value % 360;
        if (value < 0) {
            value = 359 + value;
        }
        return value;
    }


    private void initializeOtherComponents() {

        useFakeLightingCheckBox.addChangeListener(e -> selectedEntity.getModel().texture().setUseFakeLighting(useFakeLightingCheckBox.isSelected()));

        updateEntityNameButton.addActionListener(e ->{
            String name = entityNameTextField.getText();
            if (!name.trim().equals("")) {
                selectedEntity.setName(name);
                entityComboBox.updateUI();
            }
        });
    }

    private void updateEditorEntityProperties() {
        xPositionSpinner.setValue(selectedEntity.getPosition().x);
        yPositionSpinner.setValue(selectedEntity.getPosition().y);
        zPositionSpinner.setValue(selectedEntity.getPosition().z);
        xRotationSpinner.setValue(selectedEntity.getRotX());
        yRotationSpinner.setValue(selectedEntity.getRotY());
        zRotationSpinner.setValue(selectedEntity.getRotZ());
        xRotationSlider.setValue((int) selectedEntity.getRotX());
        yRotationSlider.setValue((int) selectedEntity.getRotY());
        zRotationSlider.setValue((int) selectedEntity.getRotZ());
        scaleSpinner.setValue(selectedEntity.getScale());
        scaleSlider.setValue((int) (selectedEntity.getScale()*10f));
        entityNameTextField.setText(selectedEntity.getName());
        useFakeLightingCheckBox.setSelected(selectedEntity.getModel().texture().isUsingFakeLighting());
    }
    private void updateEditorLightProperties() {

    }

}
