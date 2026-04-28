/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.mycompany.saberpro.Vista;

import com.mycompany.saberpro.Controlador.CtrlConsultasUsuarios;
import com.mycompany.saberpro.Modelo.ConsultasUsuario;
import com.mycompany.saberpro.Modelo.Roles;
import com.mycompany.saberpro.Modelo.Usuario;
import static com.mycompany.saberpro.Vista.frmMenu.jDesktopPane_menu;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.lang.Boolean.FALSE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author juanf
 */
public class InterUsuario extends javax.swing.JInternalFrame {

    private DefaultTableModel modeloTabla;
    private Usuario usuarioLogueado;

    // ====== helper: saber si el que está logueado es Decano ======
    private boolean esDecanoLogueado() {
        return usuarioLogueado != null
                && usuarioLogueado.getRol() != null
                && usuarioLogueado.getRol().getId_roles() == 3; // 3 = Decano
    }

    public InterUsuario() {
        initComponents();
        this.setResizable(false);
        this.setSize(new Dimension(1330, 444));
        this.setTitle("GESTION DE USUARIOS");
        btnActivar.setVisible(FALSE);

        // Usuario que inició sesión
        usuarioLogueado = Usuario.usuarioActual;

        // ========== Combo de la izquierda (cbxRol) ==========
        Roles cc = new Roles();
        DefaultComboBoxModel<Roles> modeloRol = new DefaultComboBoxModel<>();

        // Traemos TODOS los roles como antes
        DefaultComboBoxModel<Roles> todosLosRoles
                = new DefaultComboBoxModel<>(cc.mostrarRoles());

        if (esDecanoLogueado()) {
            // Si el logueado es Decano, solo puede ver:
            // Director programa(7), Coord(8), Comité(9), Profesor(6), Estudiante(4)
            for (int i = 0; i < todosLosRoles.getSize(); i++) {
                Roles r = todosLosRoles.getElementAt(i);
                if (r == null) {
                    continue;
                }

                int id = r.getId_roles();
                if (id == 4 || id == 6 || id == 7 || id == 8 || id == 9) {
                    modeloRol.addElement(r);
                }
            }
        } else {
            // Admin / Secretaría / otros ven todos
            modeloRol = todosLosRoles;
        }

        cbxRol.setModel(modeloRol);

        // ========= Tabla + combo de filtro de la derecha =========
        configurarTabla();
        cargarRolesEnComboTabla();   // aquí también filtramos para Decano
        configurarEventosTabla();
    }

    // ================== EVENTO TABLA ==================
    private void configurarEventosTabla() {
        jTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = jTable.getSelectedRow();
                if (fila >= 0 && modeloTabla != null) {

                    // Pasa datos de la fila a los campos + selecciona el rol correcto
                    pasarFilaATextFields(fila);

                    // Buscar el código (id_usuario) por correo para llenar txtCodigo y contraseña
                    ConsultasUsuario dao = new ConsultasUsuario();
                    Usuario u = new Usuario();
                    u.setCorreo(txtCorreo.getText());
                    if (dao.buscar(u)) {
                        txtCodigo.setText(String.valueOf(u.getId_usuario()));
                        txtContraseña.setText(u.getContrasena());
                    }
                }
            }
        });
    }

    // ================== TABLA Y COMBO DERECHA ==================
    // Inicializa el modelo de la tabla
    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"Nombre", "Apellido", "Teléfono", "CC", "Correo", "Rol"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // celdas solo lectura
            }
        };
        jTable.setModel(modeloTabla);
    }

    // helper para saber si un nombre de rol es visible para Decano en el filtro
    private boolean esRolVisibleParaDecano(String nombreRol) {
        if (nombreRol == null) {
            return false;
        }
        String n = nombreRol.toLowerCase();

        boolean esDirector = n.equals("director de programa");
        boolean esCoord = n.equals("coordinador saber pro") || n.equals("coordinador saberpro");
        boolean esComite = n.equals("comite de programa") || n.equals("comité de programa");
        boolean esProfesor = n.equals("profesor");
        boolean esEstudiante = n.equals("estudiante");

        return esDirector || esCoord || esComite || esProfesor || esEstudiante;
    }

    // Llena cbxRol1 (el combo sobre la tabla) con los roles
    private void cargarRolesEnComboTabla() {

        DefaultComboBoxModel<Roles> modeloFiltro = new DefaultComboBoxModel<>();

        // Opción "Todos"
        Roles todos = new Roles();
        todos.setId_roles(0);
        todos.setNombre("Todos");
        modeloFiltro.addElement(todos);

        ConsultasUsuario dao = new ConsultasUsuario();
        ResultSet rs = dao.obtenerRoles();   // SELECT DISTINCT rol FROM vista_usuarios

        try {
            while (rs != null && rs.next()) {
                String nombreRol = rs.getString("rol");

                Roles r = new Roles();
                r.setNombre(nombreRol);

                // Si el logueado es Decano, solo agregamos los que puede manejar
                if (esDecanoLogueado()) {
                    String n = nombreRol.toLowerCase();

                    boolean esPermitido
                            = n.equals("director de programa")
                            || n.equals("coordinador saber pro")
                            || n.equals("coordinador saberpro")
                            || n.equals("comite de programa")
                            || n.equals("comité de programa")
                            || n.equals("profesor")
                            || n.equals("estudiante");

                    if (!esPermitido) {
                        continue; // saltar este rol
                    }
                }

                modeloFiltro.addElement(r);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar roles en combo de tabla: " + e.getMessage());
        }

        cbxRol1.setModel(modeloFiltro);
    }

    // Carga en la tabla los usuarios según el rol seleccionado
    private void cargarUsuariosPorRol(String rolSeleccionado) {
        if (modeloTabla == null) {
            return;
        }

        modeloTabla.setRowCount(0); // limpiar tabla

        ConsultasUsuario dao = new ConsultasUsuario();

        // En consultarUsuarios, si rol = "Todos" no aplica filtro
        ResultSet rs = dao.consultarUsuarios("", rolSeleccionado, "Todos", null, null);

        try {
            while (rs != null && rs.next()) {
                Object[] fila = new Object[6];
                fila[0] = rs.getString("nombre");
                fila[1] = rs.getString("apellido");
                fila[2] = rs.getString("telefono");
                fila[3] = rs.getString("cc");
                fila[4] = rs.getString("correo");
                fila[5] = rs.getString("rol");

                modeloTabla.addRow(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargarUsuariosPorRol: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios por rol");
        }
    }

    // Pasa la fila seleccionada de la tabla a los textfields de la izquierda
    private void pasarFilaATextFields(int fila) {
        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        String apellido = (String) modeloTabla.getValueAt(fila, 1);
        String telefono = (String) modeloTabla.getValueAt(fila, 2);
        String cc = (String) modeloTabla.getValueAt(fila, 3);
        String correo = (String) modeloTabla.getValueAt(fila, 4);
        String rolNombre = (String) modeloTabla.getValueAt(fila, 5);

        txtNombre.setText(nombre);
        txtApellido.setText(apellido);
        txtTelefono.setText(telefono);
        txtCC.setText(cc);
        txtCorreo.setText(correo);

        // Seleccionar el rol equivalente en cbxRol (que tiene objetos Roles)
        for (int i = 0; i < cbxRol.getItemCount(); i++) {
            Roles r = cbxRol.getItemAt(i);
            if (r.getNombre().equalsIgnoreCase(rolNombre)) {
                cbxRol.setSelectedIndex(i);
                break;
            }
        }
    }

    // ================== PERMISOS DE ROLES (POR ID) ==================
    private boolean puedeAsignarRol(Roles rolDestino) {

        if (rolDestino == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol válido.");
            return false;
        }

        if (usuarioLogueado == null || usuarioLogueado.getRol() == null) {
            JOptionPane.showMessageDialog(this, "No se pudo determinar el usuario logueado.");
            return false;
        }

        int origenId = usuarioLogueado.getRol().getId_roles();  // rol del que modifica
        int destinoId = rolDestino.getId_roles();               // rol que quiere asignar

        boolean origenEsAdmin = (origenId == 1);      // Administrador
        boolean origenEsSecretaria = (origenId == 2); // Secretaria de acreditacion
        boolean origenEsDecano = (origenId == 3);     // Decano

        boolean destinoEsAdmin = (destinoId == 1);
        boolean destinoEsSecretaria = (destinoId == 2);
        boolean destinoEsDecano = (destinoId == 3);
        boolean destinoEsEstudiante = (destinoId == 4);
        boolean destinoEsProfesor = (destinoId == 6);
        boolean destinoEsDirector = (destinoId == 7);
        boolean destinoEsCoord = (destinoId == 8);
        boolean destinoEsComite = (destinoId == 9);

        // 1) Admin solo por Admin
        if (destinoEsAdmin && !origenEsAdmin) {
            JOptionPane.showMessageDialog(this,
                    "Solo un Administrador puede asignar el rol Administrador.");
            return false;
        }

        // 2) Secretaria solo por Admin
        if (destinoEsSecretaria && !origenEsAdmin) {
            JOptionPane.showMessageDialog(this,
                    "Solo un Administrador puede asignar el rol Secretaria de acreditación.");
            return false;
        }

        // 3) Decano solo por Secretaria
        if (destinoEsDecano && !origenEsSecretaria) {
            JOptionPane.showMessageDialog(this,
                    "Solo la Secretaria de acreditación puede asignar el rol Decano.");
            return false;
        }

        // 4) Director / Coord / Comité solo por Decano
        if ((destinoEsDirector || destinoEsCoord || destinoEsComite) && !origenEsDecano) {
            JOptionPane.showMessageDialog(this,
                    "Solo el Decano puede asignar Director de programa,\n"
                    + "Coordinador Saber Pro o Comité de programa.");
            return false;
        }

        // 5) Profesor / Estudiante por Admin, Secretaria o Decano
        if ((destinoEsProfesor || destinoEsEstudiante)
                && !(origenEsAdmin || origenEsSecretaria || origenEsDecano)) {
            JOptionPane.showMessageDialog(this,
                    "Solo Administrador, Secretaria de acreditación o Decano pueden asignar\n"
                    + "los roles Profesor o Estudiante.");
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtCC.setText("");
        txtContraseña.setText("");
        cbxRol.setSelectedIndex(0);
        txtCodigo.setText("");
    }

    // ================== CORREOS ==================
    private void enviarCorreoNuevoUsuario(String correoDestino,
            String nombreUsuario,
            String rolAsignado) {

        String asunto = "Creación de cuenta en el sistema SaberPro";
        String mensaje = "Hola " + nombreUsuario + ",\n\n"
                + "Se ha creado una cuenta a tu nombre en el sistema de análisis SaberPro.\n\n"
                + "Rol asignado: " + rolAsignado + "\n\n"
                + "Para ingresar por primera vez al sistema, por favor dirígete a la opción "
                + "\"Recuperar contraseña\" en la pantalla de inicio de sesión y establece "
                + "una nueva contraseña de tu preferencia.\n\n"
                + "Ten en cuenta que tu usuario de acceso es este mismo correo: " + correoDestino + "\n\n"
                + "Si consideras que este registro es un error, comunícate con el administrador del sistema.\n\n"
                + "Saludos,\n"
                + "Sistema SaberPro";

        try {
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.auth", "true");

            String correoRemitente = "saberproanalisis@gmail.com";
            String passwordRemitente = "lckcepfstlutlpga"; // contraseña de aplicación

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoRemitente, passwordRemitente);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(correoRemitente));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoDestino));
            message.setSubject(asunto);
            message.setText(mensaje);

            Transport.send(message);

            System.out.println("Correo de nuevo usuario enviado a " + correoDestino);

        } catch (MessagingException ex) {
            JOptionPane.showMessageDialog(this,
                    "El usuario se creó correctamente, pero hubo un error al enviar el correo a "
                    + correoDestino + ":\n" + ex.getMessage());
        }
    }

    private void enviarCorreoCambioRol(String correoDestino,
            String nombreUsuario,
            String rolAnterior,
            String rolNuevo) {

        String asunto = "Notificación de cambio de rol en el sistema SaberPro";
        String mensaje = "Hola " + nombreUsuario + ",\n\n"
                + "Te informamos que tu rol en el sistema de análisis SaberPro ha sido actualizado.\n\n"
                + "Rol anterior: " + rolAnterior + "\n"
                + "Rol nuevo: " + rolNuevo + "\n\n"
                + "Para ingresar al sistema, si aún no lo has hecho, puedes dirigirte a la opción "
                + "\"Recuperar contraseña\" en la pantalla de inicio de sesión y establecer "
                + "una nueva contraseña de tu preferencia.\n\n"
                + "Si tú no solicitaste este cambio o consideras que es un error, por favor comunícate "
                + "con el administrador del sistema.\n\n"
                + "Saludos,\n"
                + "Sistema SaberPro";

        try {
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.auth", "true");

            String correoRemitente = "saberproanalisis@gmail.com";
            String passwordRemitente = "lckcepfstlutlpga";

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoRemitente, passwordRemitente);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(correoRemitente));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoDestino));
            message.setSubject(asunto);
            message.setText(mensaje);

            Transport.send(message);

            System.out.println("Correo de cambio de rol enviado a " + correoDestino);

        } catch (MessagingException ex) {
            JOptionPane.showMessageDialog(this,
                    "El rol se cambió correctamente, pero hubo un error al enviar el correo a "
                    + correoDestino + ":\n" + ex.getMessage());
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

        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();
        txtCorreo = new javax.swing.JTextField();
        txtCodigo = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        txtId = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtContraseña = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtCC = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnActivar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        cbxRol1 = new javax.swing.JComboBox<>();
        cbxRol = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setResizable(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("CC");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Correo");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Contraseña");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, -1, -1));

        btnLimpiar.setBackground(new java.awt.Color(0, 0, 153));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        getContentPane().add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 350, -1, -1));

        btnEliminar.setBackground(new java.awt.Color(204, 0, 51));
        btnEliminar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Desactivar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        getContentPane().add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, -1, -1));

        btnModificar.setBackground(new java.awt.Color(0, 0, 153));
        btnModificar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnModificar.setForeground(new java.awt.Color(255, 255, 255));
        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });
        getContentPane().add(btnModificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 350, -1, -1));

        btnAgregar.setBackground(new java.awt.Color(0, 0, 153));
        btnAgregar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnAgregar.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        getContentPane().add(btnAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 350, -1, -1));

        txtCorreo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(txtCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 320, -1));

        txtCodigo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 320, 20));

        btnBuscar.setBackground(new java.awt.Color(0, 0, 153));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setText("Buscar");
        getContentPane().add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, -1, -1));

        txtId.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });
        getContentPane().add(txtId, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 60, -1, -1));

        txtApellido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(txtApellido, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 320, -1));

        txtContraseña.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(txtContraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 320, -1));

        txtNombre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 320, -1));

        txtTelefono.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(txtTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 320, -1));

        txtCC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(txtCC, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 180, 320, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Codigo");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, -1, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Nombre");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Apellido");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Telefono");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, -1));

        btnActivar.setBackground(new java.awt.Color(255, 255, 0));
        btnActivar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnActivar.setText("Activar");
        btnActivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActivarActionPerformed(evt);
            }
        });
        getContentPane().add(btnActivar, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 290, 90, -1));

        jPanel1.setBackground(new java.awt.Color(94, 122, 178));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nombre", "Apellido", "Rol"
            }
        ));
        jScrollPane1.setViewportView(jTable);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 100, 510, 270));

        jButton1.setBackground(new java.awt.Color(0, 0, 153));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Consultar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 350, -1, -1));

        cbxRol1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cbxRol1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxRol1ActionPerformed(evt);
            }
        });
        jPanel1.add(cbxRol1, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 50, 320, -1));

        cbxRol.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cbxRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxRolActionPerformed(evt);
            }
        });
        jPanel1.add(cbxRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 300, 320, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Rol");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 50, -1, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Rol");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1320, 410));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbxRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxRolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxRolActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // Tomar los datos de los campos de la UI
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String cc = txtCC.getText().trim();
        String contrasena = txtContraseña.getText().trim();
        Roles rolSeleccionado = (Roles) cbxRol.getSelectedItem();

        if (rolSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol.");
            return;
        }

        // Validar campos
        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty()
                || telefono.isEmpty() || cc.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos");
            return;
        }

        // VALIDAR PERMISOS DE ROL (si no puede, no se registra)
        if (!puedeAsignarRol(rolSeleccionado)) {
            return;
        }

        // Crear objeto Usuario con rol
        Usuario usr = new Usuario();
        usr.setNombre(nombre);
        usr.setApellido(apellido);
        usr.setCorreo(correo);
        usr.setTelefono(telefono);
        usr.setCc(cc);
        usr.setContrasena(contrasena);
        usr.setRol(rolSeleccionado);

        // Llamar al método registrar
        ConsultasUsuario consultas = new ConsultasUsuario();
        boolean exito = consultas.registrar(usr);

        if (exito) {
            limpiarCampos();
        }
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void btnActivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActivarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnActivarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Usuario mod1 = new Usuario();
        ConsultasUsuario modC1 = new ConsultasUsuario();
        InterConsultasU frm1 = new InterConsultasU();
        CtrlConsultasUsuarios ctrl1 = new CtrlConsultasUsuarios(frm1, modC1);

        jDesktopPane_menu.add(frm1);  // si tienes ese método para inicializar la vista
        frm1.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed

        if (txtCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe buscar un usuario primero.");
            return;
        }

        int idUsuario = Integer.parseInt(txtCodigo.getText());

        ConsultasUsuario dao = new ConsultasUsuario();
        Usuario usuarioActual = new Usuario();
        usuarioActual.setId_usuario(idUsuario);

        if (!dao.buscar(usuarioActual)) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar usuario actual.");
            return;
        }

        int rolOriginalId = usuarioActual.getRol().getId_roles();  // Rol actual en BD
        String rolOriginalNombre = usuarioActual.getRol().getNombre(); // para el correo

        // Rol elegido en la UI
        Roles rolNuevo = (Roles) cbxRol.getSelectedItem();
        if (rolNuevo == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol.");
            return;
        }
        int rolNuevoId = rolNuevo.getId_roles();
        String rolNuevoNombre = rolNuevo.getNombre(); // para el correo

        // 🔹 Rol del usuario LOGUEADO (quien está modificando)
        int origenId = -1;
        if (usuarioLogueado != null && usuarioLogueado.getRol() != null) {
            origenId = usuarioLogueado.getRol().getId_roles();
        }

        // ================== REGLA ESPECIAL ==================
        // Si el usuario que estoy editando ES Decano (id 3)
        // y lo quiero pasar a Profesor (6) o Estudiante (4),
        // SOLO la Secretaria (id 2) puede hacer eso.
        if (rolOriginalId == 3 && (rolNuevoId == 4 || rolNuevoId == 6)) {
            if (origenId != 2) {
                JOptionPane.showMessageDialog(this,
                        "Solo la Secretaria de acreditación puede degradar un Decano "
                        + "a Profesor o Estudiante.");
                return;
            }
        }
        // ====================================================

        boolean rolCambio = (rolNuevoId != rolOriginalId);

        // SOLO validar permisos genéricos si CAMBIÓ EL ROL
        if (rolCambio) {
            if (!puedeAsignarRol(rolNuevo)) {
                JOptionPane.showMessageDialog(this,
                        "No tienes permisos para cambiar este rol.");
                return;
            }
        }

        // Si llegó aquí → modificamos normalmente
        Usuario u = new Usuario();
        u.setId_usuario(idUsuario);
        u.setNombre(txtNombre.getText());
        u.setApellido(txtApellido.getText());
        u.setTelefono(txtTelefono.getText());
        u.setCc(txtCC.getText());
        u.setCorreo(txtCorreo.getText());
        u.setContrasena(txtContraseña.getText());
        u.setRol(rolNuevo);

        if (dao.modificar(u, idUsuario)) {
            JOptionPane.showMessageDialog(this, "Usuario modificado correctamente.");

            // Si hubo cambio de rol, enviar correo
            if (rolCambio) {
                enviarCorreoCambioRol(
                        u.getCorreo(),
                        u.getNombre(),
                        rolOriginalNombre,
                        rolNuevoNombre
                );
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al modificar usuario.");
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void cbxRol1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxRol1ActionPerformed
        if (modeloTabla == null) {
            return;
        }

        Roles seleccionado = (Roles) cbxRol1.getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        String rolSeleccionado = seleccionado.getNombre();

        if (!rolSeleccionado.equalsIgnoreCase("Todos")) {
            cargarUsuariosPorRol(rolSeleccionado);
        } else {
            cargarUsuariosPorRol("Todos");
        }
    }//GEN-LAST:event_cbxRol1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnActivar;
    public javax.swing.JButton btnAgregar;
    public javax.swing.JButton btnBuscar;
    public javax.swing.JButton btnEliminar;
    public javax.swing.JButton btnLimpiar;
    public javax.swing.JButton btnModificar;
    public javax.swing.JComboBox<Roles> cbxRol;
    public javax.swing.JComboBox<Roles> cbxRol1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    public javax.swing.JTextField txtApellido;
    public javax.swing.JTextField txtCC;
    public javax.swing.JTextField txtCodigo;
    public javax.swing.JTextField txtContraseña;
    public javax.swing.JTextField txtCorreo;
    public javax.swing.JTextField txtId;
    public javax.swing.JTextField txtNombre;
    public javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
