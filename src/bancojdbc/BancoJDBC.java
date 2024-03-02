package bancojdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

public class BancoJDBC {

    private static Connection con;
    private static Statement stmt;

    public BancoJDBC(){
        try{
            Class.forName("com.mysql.jdbc.Drive");
            System.out.println("Driver encontrado!");
        } catch(ClassNotFoundException e){
           
        }

        String url = "jdbc:mysql://localhost:3306/BANCO";
        String user = "root";
        String password = "";

        try{
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
           
        } catch(SQLException e){
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        BancoJDBC bancoJdbc = new BancoJDBC();
        bancoJdbc.mostrarMenu();
    }
   
    private void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcao;
        do {
            System.out.println("\n### Menu de Opções ###");
            System.out.println("1 - Inserir Registro");
            System.out.println("2 - Alterar Registro");
            System.out.println("3 - Consultar Registros");
            System.out.println("4 - Excluir Registro");
            System.out.println("5 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    System.out.print("Digite a matrícula: ");
                    int mat = scanner.nextInt();
                    System.out.print("Digite o nome: ");
                    scanner.nextLine();
                    String nome = scanner.nextLine();
                    System.out.print("Digite o telefone: ");
                    String telefone = scanner.next();
                    System.out.print("Digite o salário: ");
                    String salario = scanner.next();
                    inserirRegistro(mat, nome, telefone, salario);
                    break;
                case 2:
                    alterarRegistro();
                    break;
                case 3:
                    consultarRegistros();
                    break;
                case 4:
                    excluirRegistro();
                    break;
                case 5:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 5);
    }
   
    private void inserirRegistro(int mat, String nome, String tel, String sal) {
        try{
            stmt.executeUpdate("insert into Empregado values ("+mat+",'"+nome+"','"+tel+"','"+sal+"')");
        }catch(SQLException e){
            System.out.println("erro: "+e.getMessage());
        }
       
    }

    private void alterarRegistro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número de matrícula do registro que deseja alterar: ");
        int matricula = scanner.nextInt();

        try {
        ResultSet rs = stmt.executeQuery("SELECT * FROM Empregado WHERE matricula = " + matricula);
        if (!rs.next()) {
            System.out.println("Registro com matrícula " + matricula + " não encontrado.");
            return;
        }

        System.out.print("Novo nome: ");
        String novoNome = scanner.next();
        System.out.print("Novo telefone: ");
        String novoTelefone = scanner.next();
        System.out.print("Novo salário: ");
        String novoSalario = scanner.next();

        String sql = "UPDATE Empregado SET nome = ?, telefone = ?, salario = ? WHERE matricula = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, novoNome);
            pstmt.setString(2, novoTelefone);
            pstmt.setString(3, novoSalario);
            pstmt.setInt(4, matricula);
            int rowCount = pstmt.executeUpdate();
            if (rowCount > 0) {
                System.out.println("Registro alterado com sucesso.");
            } else {
                System.out.println("Nenhum registro alterado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao alterar registro: " + e.getMessage());
        }
    } catch (SQLException e) {
        System.out.println("Erro ao consultar registro: " + e.getMessage());
    }  
    }

    private void consultarRegistros() {
        try{
            ResultSet rs;
            rs = stmt.executeQuery("SELECT * FROM Empregado");
        while ( rs.next() ){
            int matricula = rs.getInt("matricula");
            String nome = rs.getString("nome");
            String telefone = rs.getString("telefone");
            float salario = rs.getFloat("salario");
            System.out.println(matricula + "\t" + nome + "\t" + telefone + "\t" + salario);
        }
        }catch(SQLException e){
            System.out.println("Erro: "+ e.getMessage());
        }
       
    }

    private void excluirRegistro() {
   
    Scanner scanner = new Scanner(System.in);
    System.out.println("### Excluir Registro ###");
    System.out.print("Digite o número de matrícula do registro que deseja excluir: ");
    int matricula = scanner.nextInt();

    try {
        ResultSet rs = stmt.executeQuery("SELECT * FROM Empregado WHERE matricula = " + matricula);
        if (!rs.next()) {
            System.out.println("Registro com matrícula " + matricula + " não encontrado.");
            return;
        }

        System.out.print("Tem certeza que deseja excluir este registro? (S/N): ");
        String confirmacao = scanner.next().toUpperCase();
        if (!confirmacao.equals("S")) {
            System.out.println("Operação de exclusão cancelada.");
            return;
        }

        int rowCount = stmt.executeUpdate("DELETE FROM Empregado WHERE matricula = " + matricula);
        if (rowCount > 0) {
            System.out.println("Registro excluído com sucesso.");
        } else {
            System.out.println("Nenhum registro excluído.");
        }
    } catch (SQLException e) {
        System.out.println("Erro ao excluir registro: " + e.getMessage());
    }
}
   }