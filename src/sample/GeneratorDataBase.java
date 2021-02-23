package sample;

import connectBD.ConnectionBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneratorDataBase {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    public static TextArea textArea = new TextArea();

    public static void ClearDB() throws SQLException {
        connection = ConnectionBase.getConnection();
        statement = ConnectionBase.getConnection().createStatement();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "DataBase is not exist!");
        alert.setTitle("Warning");
        alert.setHeaderText("Attention!");

        ResultSet resultSet = statement.executeQuery("SELECT table_name from information_schema.TABLES where TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA='tournamentstatisticsps'");

        while (resultSet.next()){
            preparedStatement = connection.prepareStatement(String.format("DROP TABLE %s", resultSet.getString(1)));
            preparedStatement.executeUpdate();
        }
        alert.showAndWait();
        connection.close();
    }

    public static ObservableList<ReaderInTable> ViewInTable() throws SQLException {
        connection = ConnectionBase.getConnection();
        statement = connection.createStatement();
        ObservableList<ReaderInTable> matrixFromDataBase = FXCollections.observableArrayList();
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery("SELECT * FROM SummAllMatrix");
            while (resultSet.next()){
                matrixFromDataBase.add(new ReaderInTable(resultSet.getString(1),
                        Math.round(resultSet.getDouble(4)/resultSet.getInt(5)*10000d)/100d,resultSet.getInt(5),
                        Math.round(resultSet.getDouble(6)/resultSet.getInt(7)*10000d)/100d,resultSet.getInt(7),
                        Math.round(resultSet.getDouble(8)/resultSet.getInt(9)*10000d)/100d,resultSet.getInt(9),
                        Math.round(resultSet.getDouble(10)/resultSet.getInt(11)*10000d)/100d,resultSet.getInt(11),
                        Math.round(resultSet.getDouble(12)/resultSet.getInt(13)*10000d)/100d,resultSet.getInt(13),
                        Math.round(resultSet.getDouble(14)/resultSet.getInt(15)*10000d)/100d,resultSet.getInt(15),
                        Math.round(resultSet.getDouble(16)/resultSet.getInt(17)*10000d)/100d,resultSet.getInt(17),
                        Math.round(resultSet.getDouble(18)/resultSet.getInt(19)*10000d)/100d,resultSet.getInt(19),
                        Math.round(resultSet.getDouble(2)/resultSet.getInt(3)*10000d)/100d, resultSet.getInt(3),
                        Math.round((resultSet.getDouble(2) + resultSet.getDouble(4) + resultSet.getDouble(6) +
                                resultSet.getDouble(8) + resultSet.getDouble(10) + resultSet.getDouble(12) + resultSet.getDouble(14)+
                                resultSet.getDouble(16) + resultSet.getDouble(18))/(resultSet.getInt(3) + resultSet.getInt(5) +
                                resultSet.getInt(7) + resultSet.getInt(9) + resultSet.getInt(11) + resultSet.getInt(13) +
                                resultSet.getInt(15) + resultSet.getInt(17) + resultSet.getInt(19))*10000d)/100d,
                        resultSet.getInt(3) + resultSet.getInt(5) + resultSet.getInt(7) + resultSet.getInt(9) +
                                resultSet.getInt(11) + resultSet.getInt(13) + resultSet.getInt(15) + resultSet.getInt(17) +
                                resultSet.getInt(19),
                        resultSet.getDouble(20)));
            }
        } catch (SQLException ignored) { }
        return matrixFromDataBase;
    }

    public static  List<File> CheckingTournaments(List<File> files, String myName) throws SQLException, IOException {
        connection = ConnectionBase.getConnection();
        statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS filenamestable (filename VARCHAR(70))");
        List<File> newFiles = new ArrayList<>();
        String[] words1;

        for (File file: files) {
            preparedStatement = connection.prepareStatement("SELECT * FROM filenamestable WHERE filename = ?");
            preparedStatement.setString(1, file.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                textArea.appendText(file.getName() + " - already exist in DataBase\n");

            } else {
                int counter = 0;
                BufferedReader reader1 = new BufferedReader(new FileReader(file));
                ArrayList<String> deals1 = new ArrayList<>();
                String line;
                while ((line = reader1.readLine()) != null && counter < 17) {
                    if (!line.equals("")) {
                        deals1.add(line);
                    }
                    counter++;
                }

                counter = 0;
                for (String s : deals1) {
                    words1 = s.split("\\s*(\\s|,|#|№|-|:)\\s*");

                    if (words1[0].equals("Table")&& !words1[3].equals("9")){
                        GeneratorDataBase.textArea.appendText(file.getName()+ " - This tournament is " + words1[3] + "-max\n");
                        break;
                    }

                    if (words1[0].equals("Seat") && words1[2].equals(myName)){
                        newFiles.add(file);
                        preparedStatement = connection.prepareStatement("INSERT INTO filenamestable (filename) VALUE(?)");
                        preparedStatement.setString(1, file.getName());
                        preparedStatement.executeUpdate();
                        textArea.appendText(file.getName() + " - is added\n");
                        break;
                    } else if (counter == 14){
                        textArea.appendText(file.getName() + " Player with name - " + myName + " didn`t play in this Tournament\n");
                        break;
                    }
                    counter++;
                }
            }
        }
        connection.close();
        return newFiles;
    }

    public static void AddStatToDataBase (List<MatrixOfDeals> matricesState, List<String> yourHand, double[] sumChips) throws SQLException {                 //create table in database and add matrix
        connection = ConnectionBase.getConnection();
        statement = connection.createStatement();
        int[][] matrixState = new int[169][18];
        String sql;
        Alert alertError = new Alert(Alert.AlertType.ERROR, "Error");
        alertError.setTitle("Error");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS SummAllMatrix (\n" +
                "    YourHand VARCHAR(3),\n" +
                "    ButtonWinn INT,\n" +
                "    ButtonAllOpen INT,\n" +
                "    SBWinn INT,\n" +
                "    SBAllOpen INT,\n" +
                "    BBWinn INT,\n" +
                "    BBAllOpen INT,\n" +
                "    UTGWinn INT,\n" +
                "    UTGAllOpen INT,\n" +
                "    UTG1Winn INT,\n" +
                "    UTG1AllOpen INT,\n" +
                "    MPWinn INT,\n" +
                "    MPAllOpen INT,\n" +
                "    MP1Winn INT,\n" +
                "    MP1AllOpen INT,\n" +
                "    HighJackWinn INT,\n" +
                "    HighJackAllOpen INT,\n" +
                "    COWinn INT,\n" +
                "    COAllOpen INT,\n" +
                "    SUMChips DOUBLE,\n" +
                "    RowIndex INT PRIMARY KEY AUTO_INCREMENT\n" +
                "\n" +
                ")");
// Створити таблицю, яка буде сумувати певну кількість таблиць (матриць)
        ResultSet resultSet = statement.executeQuery("SELECT * FROM summallmatrix LIMIT 1");
        if (resultSet.next()){                                                                                          //table summallmatrix is clear or not?
            for (MatrixOfDeals matricesOfDeal : matricesState) {                                                        //rewrite matrix in table
// Створити і вставати сюди метод, який створює окремі таблиці в базі даних
                for (int i = 0; i < 169; i++) {
                    if (matricesState.indexOf(matricesOfDeal) != matricesState.size()-1){
                        for (int j = 0; j < 18; j++) {
                            matrixState[i][j] += matricesOfDeal.getMatrixTournament()[i][j];
                        }
                    }else {
                        try {
                            sql = "UPDATE SummAllMatrix SET ButtonWinn = ButtonWinn + ?, ButtonAllOpen = ButtonAllOpen + ?, " +
                                    "SBWinn = SBWinn + ?, SBAllOpen = SBAllOpen + ?, BBWinn = BBWinn + ?, BBAllOpen = BBAllOpen + ?, " +
                                    "UTGWinn = UTGWinn + ?, UTGAllOpen = UTGAllOpen + ?, UTG1Winn = UTG1Winn + ?, " +
                                    "UTG1AllOpen = UTG1AllOpen + ?, MPWinn = MPWinn + ?, MPAllOpen = MPAllOpen + ?, " +
                                    "MP1Winn = MP1Winn + ?, MP1AllOpen = MP1AllOpen + ?, HighJackWinn = HighJackWinn + ?, " +
                                    "HighJackAllOpen = HighJackAllOpen + ?, COWinn = COWinn + ?, COAllOpen = COAllOpen + ?, " +
                                    "SUMChips = SUMChips + ? WHERE RowIndex = ?";
                            preparedStatement = connection.prepareStatement(sql);

                            for (int j = 0; j < 18; j++) {
                                matrixState[i][j] += matricesOfDeal.getMatrixTournament()[i][j];
                                preparedStatement.setInt(j+1, matrixState[i][j]);
                            }
                            preparedStatement.setDouble(19, sumChips[i]);
                            preparedStatement.setInt(20, i+1);
                            preparedStatement.executeUpdate();
                        }catch (SQLException ex)
                        {
                            alertError.setHeaderText("Got an exception!");
                            alertError.setContentText(ex.getMessage());
                            alertError.showAndWait();
                        }
                    }
                }
            }
            textArea.appendText("Database rewritten!\n");
        } else {
            for (MatrixOfDeals matricesOfDeal : matricesState) {                                                        //write matrix in table
//Вставати сюди метод, який створює окремі таблиці в базі даних
                for (int i = 0; i < 169; i++) {
                    if (matricesState.indexOf(matricesOfDeal) != matricesState.size()-1){
                        for (int j = 0; j < 18; j++) {
                            matrixState[i][j] += matricesOfDeal.getMatrixTournament()[i][j];
                        }
                    }else {
                        try {
                            sql = "INSERT INTO SummAllMatrix(" +
                                    "YourHand, ButtonWinn, ButtonAllOpen, SBWinn, SBAllOpen, " +
                                    "BBWinn, BBAllOpen, UTGWinn, UTGAllOpen, UTG1Winn, " +
                                    "UTG1AllOpen, MPWinn, MPAllOpen, MP1Winn, MP1AllOpen, " +
                                    "HighJackWinn, HighJackAllOpen, COWinn, COAllOpen, SUMChips)" +
                                    " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                            preparedStatement = connection.prepareStatement(sql);

                            preparedStatement.setString(1, yourHand.get(i));

                            for (int j = 0; j < 18; j++) {
                                matrixState[i][j] += matricesOfDeal.getMatrixTournament()[i][j];
                                preparedStatement.setInt(j+2, matrixState[i][j]);
                            }
                            preparedStatement.setDouble(20, sumChips[i]);

                            preparedStatement.executeUpdate();
                        }catch (SQLException ex)
                        {
                            alertError.setHeaderText("Got an exception!");
                            alertError.setContentText(ex.getMessage());
                            alertError.showAndWait();
                        }
                    }
                }
            }
            textArea.appendText("DataBase created!");
        }

        connection.close();

    }
}
