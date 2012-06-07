package dk.frv.enav.esd.gui.settingtabs;

import java.awt.Color;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.frv.enav.esd.settings.AisSettings;
import dk.frv.enav.esd.settings.AisSettings.SensorConnectionType;

public class AisSettingsPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldAisHostOrSerialPort;
	private AisSettings aisSettings;
	private JComboBox comboBoxAisConnectionType;
	private JSpinner spinnerAisTcpPort;
	private JCheckBox chckbxAllowSending;
	private JCheckBox chckbxStrictTimeout;
	
	
	public AisSettingsPanel(){
		super();
		
		setBackground(GuiStyler.backgroundColor);
		setBounds(10, 11, 493, 600);
		setLayout(null);
		
		JPanel aisConnection = new JPanel();
		aisConnection.setBackground(GuiStyler.backgroundColor);
		aisConnection.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "AIS Connection", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
		
		aisConnection.setBounds(10, 11, 473, 117);
		add(aisConnection);
		aisConnection.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Connection type:");
		GuiStyler.styleText(lblNewLabel);
		lblNewLabel.setBounds(10, 22, 114, 14);
		aisConnection.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Host or serial port:");
		GuiStyler.styleText(lblNewLabel_1);
		lblNewLabel_1.setBounds(10, 46, 114, 14);
		aisConnection.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("TCP Port:");
		GuiStyler.styleText(lblNewLabel_2);
		lblNewLabel_2.setBounds(10, 68, 46, 14);
		aisConnection.add(lblNewLabel_2);
		
		comboBoxAisConnectionType = new JComboBox();
		GuiStyler.styleDropDown(comboBoxAisConnectionType);
		comboBoxAisConnectionType.setModel(new DefaultComboBoxModel(AisSettings.SensorConnectionType.values()));
		comboBoxAisConnectionType.setBounds(134, 19, 142, 20);
		aisConnection.add(comboBoxAisConnectionType);
		
		textFieldAisHostOrSerialPort = new JTextField();
		GuiStyler.styleTextFields(textFieldAisHostOrSerialPort);
		textFieldAisHostOrSerialPort.setBounds(134, 43, 142, 20);
		aisConnection.add(textFieldAisHostOrSerialPort);
		textFieldAisHostOrSerialPort.setColumns(10);
		
		spinnerAisTcpPort = new JSpinner();
		GuiStyler.styleSpinner(spinnerAisTcpPort);
		spinnerAisTcpPort.setBounds(134, 65, 142, 20);
		aisConnection.add(spinnerAisTcpPort);
		
		JPanel transponderSettings = new JPanel();
		
		transponderSettings.setBackground(GuiStyler.backgroundColor);
		transponderSettings.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "Transponder Settings", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
		transponderSettings.setBounds(10, 150, 472, 100);
		add(transponderSettings);
		transponderSettings.setLayout(null);
		
		chckbxAllowSending = new JCheckBox("Allow Sending");
		GuiStyler.styleCheckbox(chckbxAllowSending);
		chckbxAllowSending.setBounds(6, 27, 125, 23);
		transponderSettings.add(chckbxAllowSending);
		
		
		chckbxStrictTimeout = new JCheckBox("Strict timeout");
		GuiStyler.styleCheckbox(chckbxStrictTimeout);
		chckbxStrictTimeout.setBounds(6, 53, 97, 23);
		transponderSettings.add(chckbxStrictTimeout);
	
	}
	
	
	public void loadSettings(AisSettings aisSettings) {
		this.aisSettings = aisSettings;
		comboBoxAisConnectionType.getModel().setSelectedItem(aisSettings.getAisConnectionType());
		textFieldAisHostOrSerialPort.setText(aisSettings.getAisHostOrSerialPort());
		spinnerAisTcpPort.setValue(aisSettings.getAisTcpPort());

		chckbxAllowSending.setSelected(aisSettings.isAllowSending());
		chckbxStrictTimeout.setSelected(aisSettings.isStrict());
		
	}
	
	public void saveSettings() {
		aisSettings.setAisConnectionType((SensorConnectionType) comboBoxAisConnectionType.getModel().getSelectedItem());
		aisSettings.setAisHostOrSerialPort(textFieldAisHostOrSerialPort.getText());
		aisSettings.setAisTcpPort((Integer) spinnerAisTcpPort.getValue());
		
		aisSettings.setAllowSending(chckbxAllowSending.isSelected());
		aisSettings.setStrict(chckbxStrictTimeout.isSelected());
		
	}

}
