
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Vector;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jesus
 */
public class HiloEscucha extends Thread {

    MulticastSocket sock = null;
    JFrm_Chat form = null;
    private static Vector<String> listaUsuario = new Vector<String>();
    private static Vector<jFrm_ChatPrivado> listaConversacionesPrivadas;

    public HiloEscucha(MulticastSocket sock, JFrm_Chat form) {
        this.sock = sock;
        this.form = form;
        
        listaConversacionesPrivadas = new Vector<jFrm_ChatPrivado>();
    }

    public void run() {
        try {
            do {
                byte buffer[] = new byte[64];
                DatagramPacket mentrada = new DatagramPacket(buffer, buffer.length);
                sock.receive(mentrada);
                control(form, mentrada, "");
            } while (true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*separador por dos puntos (:)
0:mensaje util
1:usuario nuevo
2:sesion terminada
3:chat privado (el nombre del usuario al que se le va a mandar el mensaje sale en la tercera posición)
     */
    public static void control(JFrm_Chat form, DatagramPacket mentrada, String userx) {
        try {
            String dato = new String(mentrada.getData());
            String[] x = dato.split(":");
            String user = x[0].trim();
            int bandera = Integer.parseInt(x[1].trim());
            switch (bandera) {
                case 0:
                    formatoConversacion(form, dato, userx);
                    break;
                case 1:
                    if (!listaUsuario.contains(user)) {
                        form.preferido.addElement(user);
                        listaUsuario.add(user);
                    }
                    break;
                case 2:
                    listaUsuario.remove(user);
                    form.preferido.clear();
                    for (int i = 0; i <= listaUsuario.size(); i++) {
                        form.preferido.addElement(listaUsuario.get(i));
                    }
                    break;
                case 3:
                    String usuarioDestino = x[3].trim();
                    if(existeChatPrivadoEntreUsuarios(user, usuarioDestino)) {
                        jFrm_ChatPrivado ventanaChatPrivado = obtenerChatPrivadoConUsuario(user, usuarioDestino);
                        ventanaChatPrivado.requestFocus();
                        ventanaChatPrivado.setVisible(true);
                        ventanaChatPrivado.formatoConversacion(dato, usuarioDestino);
                    } else {
                        jFrm_ChatPrivado ventanaChatPrivado = new jFrm_ChatPrivado(user, usuarioDestino, form);
                        HiloEscucha.agregarNuevaConversacionPrivada(ventanaChatPrivado);
                        ventanaChatPrivado.setVisible(true);
                        ventanaChatPrivado.formatoConversacion(dato, usuarioDestino);
                    }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static boolean existeChatPrivadoEntreUsuarios(String usuarioInicial, String usuarioDestino) {
        for(int i = 0; i < listaConversacionesPrivadas.size(); i++) {
            jFrm_ChatPrivado ventana = listaConversacionesPrivadas.get(i);
            if(usuarioDestino.equals(ventana.usuarioDestino) && usuarioInicial.equals(ventana.usuarioInicial)) {
                return true;
            }
        }
        return false;
    }
    
    public static void agregarNuevaConversacionPrivada(jFrm_ChatPrivado nuevoChat) {
        listaConversacionesPrivadas.add(nuevoChat);
    }
    
    public static jFrm_ChatPrivado obtenerChatPrivadoConUsuario(String usuarioInicial, String usuarioDestino) {
        for(int i = 0; i < listaConversacionesPrivadas.size(); i++) {
            jFrm_ChatPrivado ventana = listaConversacionesPrivadas.get(i);
            if(usuarioDestino.equals(ventana.usuarioDestino) && usuarioInicial.equals(ventana.usuarioInicial)) {
                return listaConversacionesPrivadas.get(i);
            }
        }
        return null;
    }

    public static void formatoConversacion(JFrm_Chat form, String dato, String userx) throws Exception {
        javax.swing.JTextPane txt_conversacion = form.jTxtPn_conversacion;
        String[] x = dato.split(":");
        String user = x[0].trim();
        String mensaje = x[2].trim();
        if (!userx.equals(user)) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setBold(attrs, true);
            txt_conversacion.getStyledDocument().insertString(txt_conversacion.getStyledDocument().getLength(), user + ": ", attrs);
            StyleConstants.setBold(attrs, false);
            txt_conversacion.getStyledDocument().insertString(txt_conversacion.getStyledDocument().getLength(), mensaje + System.getProperty("line.separator"), attrs);
            if (!listaUsuario.contains(user)) {
                form.preferido.addElement(user);
                listaUsuario.add(user);
            }
        }
    }

}
