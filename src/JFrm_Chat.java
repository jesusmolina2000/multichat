
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jesus
 */
public class JFrm_Chat extends javax.swing.JFrame {
    DefaultListModel preferido = new DefaultListModel();
    String user= null ;
    InetAddress grupo = null;
    MulticastSocket sock = null;
    InetAddress ipPersonal = null;
    
    /**
     * Creates new form JFrm_Chat
     */
    public JFrm_Chat() {
        initComponents();
        iniciarFormulario();
    }
    
    private void iniciarFormulario(){
        try{
            grupo = InetAddress.getByName("224.0.0.4");
            sock = new MulticastSocket(6789);
            sock.joinGroup(grupo);
            validarUsuario();
            //hilo
            HiloEscucha hilo = new HiloEscucha(sock, this);
            hilo.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void validarUsuario(){
        try{
            user = JOptionPane.showInputDialog("digite el usuario");
            this.jLbl_Usuario.setText(user);
            String mcompleto = user + ":1:";
            byte[] m = mcompleto.getBytes();
            DatagramPacket msalida = new DatagramPacket(m,m.length, grupo, 6789);
            sock.send(msalida);
        }catch(Exception  ex){
            ex.printStackTrace();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTxtPn_Mensaje_a_Enviar = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTxtPn_conversacion = new javax.swing.JTextPane();
        jBttn_Enviar_Mensaje = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jLst_ususarios = new javax.swing.JList<>();
        jLbl_Usuario = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel1.setText("conversacion ");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 20, 110, 20);

        jLabel2.setText("usuarios");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(390, 30, 90, 20);

        jScrollPane1.setViewportView(jTxtPn_Mensaje_a_Enviar);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 350, 250, 60);

        jTxtPn_conversacion.setEditable(false);
        jScrollPane2.setViewportView(jTxtPn_conversacion);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(10, 50, 250, 290);

        jBttn_Enviar_Mensaje.setText("enviar");
        jBttn_Enviar_Mensaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBttn_Enviar_MensajeActionPerformed(evt);
            }
        });
        getContentPane().add(jBttn_Enviar_Mensaje);
        jBttn_Enviar_Mensaje.setBounds(280, 360, 160, 40);

        jLst_ususarios.setModel(preferido);
        jLst_ususarios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jLst_ususarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLst_ususariosMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jLst_ususarios);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(285, 50, 150, 290);

        jLbl_Usuario.setText("jLabel3");
        getContentPane().add(jLbl_Usuario);
        jLbl_Usuario.setBounds(170, 20, 70, 16);

        setSize(new java.awt.Dimension(473, 457));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBttn_Enviar_MensajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBttn_Enviar_MensajeActionPerformed
        try {
            String mensaje= jTxtPn_Mensaje_a_Enviar.getText()+System.getProperty("line.separator");
            String mcompleto = user + ":0:" +mensaje;
            byte[] m = mcompleto.getBytes();
            DatagramPacket msalida= new DatagramPacket(m, m.length,grupo,6789);
            sock.send(msalida);
            HiloEscucha.formatoConversacion(this, mcompleto, user);
            jTxtPn_Mensaje_a_Enviar.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jBttn_Enviar_MensajeActionPerformed

    private void jLst_ususariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLst_ususariosMouseClicked
        // Revisa si pasa doble click
        JList list = (JList)evt.getSource();
        if (evt.getClickCount() == 2) {
            String usuario2 = list.getSelectedValue().toString();
            if(!usuario2.equals(user)) {
                if(!HiloEscucha.existeChatPrivadoEntreUsuarios(user, usuario2)) {
                    jFrm_ChatPrivado ventanaChatPrivado = new jFrm_ChatPrivado(user, usuario2, this);
                    HiloEscucha.agregarNuevaConversacionPrivada(ventanaChatPrivado);
                    ventanaChatPrivado.requestFocus();
                    ventanaChatPrivado.setVisible(true);
                }
                else {
                    jFrm_ChatPrivado ventanaChatPrivado = HiloEscucha.obtenerChatPrivadoConUsuario(user, usuario2);
                    ventanaChatPrivado.requestFocus();
                    ventanaChatPrivado.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_jLst_ususariosMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrm_Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrm_Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrm_Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrm_Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrm_Chat().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBttn_Enviar_Mensaje;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLbl_Usuario;
    public javax.swing.JList<String> jLst_ususarios;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextPane jTxtPn_Mensaje_a_Enviar;
    public javax.swing.JTextPane jTxtPn_conversacion;
    // End of variables declaration//GEN-END:variables
}
