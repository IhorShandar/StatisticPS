package sample;

import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller extends Application {

    @FXML
    private Button genStatistics;

    @FXML
    private Button clearDataBase;

    @FXML
    private TableColumn<ReaderInTable, Double> MyHandColumn;

    @FXML
    private TableColumn<ReaderInTable, Double> SBWin;

    @FXML
    private TableColumn<ReaderInTable, Double> SBAll;

    @FXML
    private TableColumn<ReaderInTable, Double> BBWin;

    @FXML
    private TableColumn<ReaderInTable, Double> BBAll;

    @FXML
    private TableColumn<ReaderInTable, Double> UTGWin;

    @FXML
    private TableColumn<ReaderInTable, Double> UTGAll;

    @FXML
    private TableColumn<ReaderInTable, Double> UTG1Win;

    @FXML
    private TableColumn<ReaderInTable, Double> UTG1All;

    @FXML
    private TableColumn<ReaderInTable, Double> MPWin;

    @FXML
    private TableColumn<ReaderInTable, Double> MPAll;

    @FXML
    private TableColumn<ReaderInTable, Double> MP1Win;

    @FXML
    private TableColumn<ReaderInTable, Double> MP1All;

    @FXML
    private TableColumn<ReaderInTable, Double> HighJackWin;

    @FXML
    private TableColumn<ReaderInTable, Double> HighJackAll;

    @FXML
    private TableColumn<ReaderInTable, Double> COWin;

    @FXML
    private TableColumn<ReaderInTable, Double> COAll;

    @FXML
    private TableColumn<ReaderInTable, Double> ButtonWin;

    @FXML
    private TableColumn<ReaderInTable, Double> ButtonAll;

    @FXML
    private TableColumn<ReaderInTable, Double> AllHandWin;

    @FXML
    private TableColumn<ReaderInTable, Double> AllHand;

    @FXML
    private TableColumn<ReaderInTable, Double> SUMChips;

    @FXML
    private TableView<ReaderInTable> tableStatistics;

    @FXML
    private TextField namePS;

    @FXML
    private ProgressBar ProgressStatus;

    private static ProgressBar progressBar;

    int buttonPosition;
    int myPositionIndex;
    List<Integer> indexHand = new ArrayList<>();
    String myPositionName = null;
    String myName;
    char[] myCards;
    Set<Character> rangCards = new LinkedHashSet<>(Arrays.asList('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'));
    List<String> positions = new ArrayList<>(Arrays.asList("Button", "SB", "BB", "UTG", "UTG+1", "MP", "MP+1", "HJ", "CO"));
    String[] words;
    boolean takePartAtDeal;
    String myCardsInMatrix;

    List<String> positionsInMatrix = new ArrayList<>(Arrays.asList("", "Button", "", "SB", "", "BB", "", "UTG", "", "UTG+1", "", "MP", "", "MP+1", "", "HJ", "", "CO"));
    List<String> namesRowInMatrix = new ArrayList<>(Arrays.asList("AKs", "AQs", "AJs", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s", "KQs", "KJs", "KTs", "K9s", "K8s", "K7s", "K6s",
            "K5s", "K4s", "K3s", "K2s", "QJs", "QTs", "Q9s", "Q8s", "Q7s", "Q6s", "Q5s", "Q4s", "Q3s", "Q2s", "JTs", "J9s", "J8s", "J7s", "J6s", "J5s", "J4s",
            "J3s", "J2s", "T9s", "T8s", "T7s", "T6s", "T5s", "T4s", "T3s", "T2s", "98s", "97s", "96s", "95s", "94s", "93s", "92s", "87s", "86s", "85s", "84s",
            "83s", "82s", "76s", "75s", "74s", "73s", "72s", "65s", "64s", "63s", "62s", "54s", "53s", "52s", "43s", "42s", "32s", "AAo", "KKo", "QQo", "JJo",
            "TTo", "99o", "88o", "77o", "66o", "55o", "44o", "33o", "22o", "AKo", "AQo", "AJo", "ATo", "A9o", "A8o", "A7o", "A6o", "A5o", "A4o", "A3o", "A2o",
            "KQo", "KJo", "KTo", "K9o", "K8o", "K7o", "K6o", "K5o", "K4o", "K3o", "K2o", "QJo", "QTo", "Q9o", "Q8o", "Q7o", "Q6o", "Q5o", "Q4o", "Q3o", "Q2o",
            "JTo", "J9o", "J8o", "J7o", "J6o", "J5o", "J4o", "J3o", "J2o", "T9o", "T8o", "T7o", "T6o", "T5o", "T4o", "T3o", "T2o", "98o", "97o", "96o", "95o",
            "94o", "93o", "92o", "87o", "86o", "85o", "84o", "83o", "82o", "76o", "75o", "74o", "73o", "72o", "65o", "64o", "63o", "62o", "54o", "53o", "52o",
            "43o", "42o", "32o"));

    List<MatrixOfDeals> matrixOfDeals = new ArrayList<>();
    long numberTournament;
    double[] sumChips = new double[169];

    private int[][] GenerationStatistic(List<String> deal, List<Integer> positionsDeals) {                             // generate statistics
        int indexColumn = 0;
        int indexRaw = 0;
        int[][] matrixOFNumberHand = new int[169][18];
        double chipsPost, ante, oneBB = 0;

        for (int index1 = 0; index1 < positionsDeals.size() - 1; index1++) {
            int firstEntryName = 0;
            myPositionName = null;
            myCardsInMatrix = null;
            takePartAtDeal = false;
            chipsPost = ante = 0;
            for (int index = positionsDeals.get(index1); index < positionsDeals.get(index1 + 1); index++) {
                words = deal.get(index).split("\\s*(\\s|,|/|№|\\(|\\)|#|]|\\[|-|:)\\s*");

                if (words[1].equals("Hand")){
                    if (words[5].equals("Freeroll")){
                        oneBB = Double.parseDouble(words[12]);
                    } else {
                        oneBB = Double.parseDouble(words[13]);
                    }
                }


                if (words[0].equals("Table")) {                                                  // search position of button
                    buttonPosition = Integer.parseInt(words[words.length - 4]);
                }

                if (words[0].equals("Seat") && words[2].equals(myName) && firstEntryName == 0) {                         //search own position
                    myPositionIndex = Integer.parseInt(words[1]);
                    if (myPositionIndex - buttonPosition >= 0) {
                        myPositionName = positions.get(myPositionIndex - buttonPosition);
                    } else if (myPositionIndex - buttonPosition < 0) {
                        myPositionName = positions.get(positions.size() + (myPositionIndex - buttonPosition));
                    }
                    firstEntryName++;
                }

                if (words[0].equals(myName) && (words[1].equals("posts"))) {
                    ante += Double.parseDouble(words[4]);
                }

                if (words[0].equals("Dealt")) {                                                                          //search own Cards
                    myCards = (words[3] + words[4]).toCharArray();
                    String s = "o";
                    if (myCards[1] == myCards[3]) {
                        s = "s";
                    }

                    for (Character c : rangCards) {
                        if (c == myCards[0]) {
                            myCardsInMatrix = String.valueOf(myCards[0]) + myCards[2] + s;
                            break;
                        } else if (c == myCards[2]) {
                            myCardsInMatrix = String.valueOf(myCards[2]) + myCards[0] + s;
                            break;
                        }
                    }
                }

                if (words[0].equals(myName) && (words[1].equals("calls") || words[1].equals("raises") || words[1].equals("checks") || words[1].equals("bets"))) {
                    takePartAtDeal = true;
                    if (words[1].equals("calls") || words[1].equals("bets")) {
                        chipsPost += Double.parseDouble(words[2]);
                    } else if (words[1].equals("raises")) {
                        chipsPost += Double.parseDouble(words[4]);
                    }
                }

                if (words[0].equals("Uncalled") && words[words.length - 1].equals(myName)) {
                    chipsPost -= Double.parseDouble(words[2]);
                }

                if (takePartAtDeal && myCardsInMatrix != null && myPositionName != null) {
                    for (String s : positionsInMatrix) {
                        if (s.equals(myPositionName)) {
                            indexColumn = positionsInMatrix.indexOf(s);
                            break;
                        }
                    }

                    for (String s : namesRowInMatrix) {
                        if (s.equals(myCardsInMatrix)) {
                            indexRaw = namesRowInMatrix.indexOf(s);
                            break;
                        }
                    }

                    if (firstEntryName == 1 && words[0].equals("Seat") && words[2].equals(myName)) {                      //filling the matrix
                        chipsPost += ante;
                        chipsPost *= (-1);
                        matrixOFNumberHand[indexRaw][indexColumn] += 1;

                        int indx = 0;
                        for (String s : words) {
                            double allwinChips;
                            if (s.equals("collected") || s.equals("won")) {
                                allwinChips = Double.parseDouble(words[indx + 1]);
                                matrixOFNumberHand[indexRaw][indexColumn - 1] += 1;
                                chipsPost = allwinChips + chipsPost;
                            }
                            indx++;
                        }
                        chipsPost = chipsPost/oneBB;
                        sumChips[indexRaw] += chipsPost;
                        //    System.out.println(myCardsInMatrix + " " + indexRaw + " and " + indexColumn + "; chipsWin = " + chipsPost);
                        firstEntryName++;
                    }
                }


            }
        }
        return matrixOFNumberHand;
    }

    private void GenerationTableView() throws SQLException {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

        List<TableColumn<ReaderInTable, Double>> str = new ArrayList<>();
        str.add(SBWin);
        str.add(BBWin);
        str.add(UTGWin);
        str.add(UTG1Win);
        str.add(MPWin);
        str.add(MP1Win);
        str.add(HighJackWin);
        str.add(COWin);
        str.add(ButtonWin);
        str.add(AllHandWin);

        MyHandColumn.setCellValueFactory(new PropertyValueFactory<>("nameHand"));
        SBWin.setCellValueFactory(new PropertyValueFactory<>("SBWin"));
        BBWin.setCellValueFactory(new PropertyValueFactory<>("BBWin"));
        UTGWin.setCellValueFactory(new PropertyValueFactory<>("UTGWin"));
        UTG1Win.setCellValueFactory(new PropertyValueFactory<>("UTG1Win"));
        MPWin.setCellValueFactory(new PropertyValueFactory<>("MPWin"));
        MP1Win.setCellValueFactory(new PropertyValueFactory<>("MP1Win"));
        HighJackWin.setCellValueFactory(new PropertyValueFactory<>("HighJackWin"));
        COWin.setCellValueFactory(new PropertyValueFactory<>("COWin"));
        ButtonWin.setCellValueFactory(new PropertyValueFactory<>("ButtonWin"));
        AllHandWin.setCellValueFactory(new PropertyValueFactory<>("AllHandWin"));

        for (TableColumn<ReaderInTable, Double> tableColumn : str) {
            tableColumn.setCellFactory(column -> new TableCell<ReaderInTable, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {

                    if (!empty) {
                        setText(df.format(item) + "%");
                        if (item >= 80) {
                            setStyle("-fx-background-color: #48FF00");
                            setTextFill(Color.BROWN);
                        }else if (item >= 60 && item < 80) {
                            setStyle("-fx-background-color: #8AFF5C");
                            setTextFill(Color.BROWN);
                        } else if (item >= 50 && item < 60) {
                            setStyle("-fx-background-color: #C1FDA9");
                            setTextFill(Color.BROWN);
                        } else if (item >= 30 && item < 50) {
                            setStyle("-fx-background-color: #FF775A");
                            setTextFill(Color.BLACK);
                        } else if (item >= 15 && item < 30) {
                            setStyle("-fx-background-color: #FF461F");
                            setTextFill(Color.BLACK);
                        }else if (item > 0 && item < 15) {
                            setStyle("-fx-background-color: red");
                            setTextFill(Color.BLACK);
                        } else {
                            setStyle("");
                            setTextFill(Color.BLACK);
                        }
                    }
                }
            });
        }

        SBAll.setCellValueFactory(new PropertyValueFactory<>("SBAll"));
        BBAll.setCellValueFactory(new PropertyValueFactory<>("BBAll"));
        UTGAll.setCellValueFactory(new PropertyValueFactory<>("UTGAll"));
        UTG1All.setCellValueFactory(new PropertyValueFactory<>("UTG1All"));
        MPAll.setCellValueFactory(new PropertyValueFactory<>("MPAll"));
        MP1All.setCellValueFactory(new PropertyValueFactory<>("MP1All"));
        HighJackAll.setCellValueFactory(new PropertyValueFactory<>("HighJackAll"));
        COAll.setCellValueFactory(new PropertyValueFactory<>("COAll"));
        ButtonAll.setCellValueFactory(new PropertyValueFactory<>("ButtonAll"));
        AllHand.setCellValueFactory(new PropertyValueFactory<>("AllHand"));

        SUMChips.setCellFactory(column -> new TableCell<ReaderInTable, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {

                if (!empty) {
                    setAlignment(Pos.CENTER);
                    setText(df.format(item));

                    if (item >= 200) {
                        setStyle("-fx-background-color: #48FF00");
                        setTextFill(Color.BROWN);
                    }else if (item >= 100 && item < 200) {
                        setStyle("-fx-background-color: #8AFF5C");
                           setTextFill(Color.BROWN);
                    } else if (item >= 50 && item < 100) {
                        setStyle("-fx-background-color: #C1FDA9");
                        setTextFill(Color.BROWN);
                    } else if (item >= 0 && item < 50) {
                        setStyle("-fx-background-color: #E3FFD8");
                        setTextFill(Color.BROWN);
                    } else if (item >= -50 && item < 0) {
                        setStyle("-fx-background-color: #FFC7C7");
                        setTextFill(Color.BLACK);
                    } else if (item >= -100 && item < -50) {
                        setStyle("-fx-background-color: #FF775A");
                        setTextFill(Color.BLACK);
                    }else if (item >= -200 && item < -100) {
                        setStyle("-fx-background-color: #FF461F");
                        setTextFill(Color.BLACK);
                    }else if (item < -200) {
                        setStyle("-fx-background-color: red");
                        setTextFill(Color.BLACK);
                    } else {
                        setStyle("");
                        setTextFill(Color.BLACK);
                    }
                }
            }
        });

        SUMChips.setCellValueFactory(new PropertyValueFactory<>("SumChips"));


        tableStatistics.setItems(GeneratorDataBase.ViewInTable());
    }

    private List<Path> listFiles(File path) {
        List<Path> result = new ArrayList<>();
        if (path.isDirectory()) {
            try (Stream<Path> walk = Files.walk(path.toPath())) {
                result = walk
                        .filter(f -> !f.startsWith("C:\\MyHandsArchive_H2N\\Summaries"))
                        .filter(Files::isRegularFile)
                        .filter(file -> file.getFileName().toString().endsWith(".txt"))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    @FXML
    void initialize() throws SQLException {
        namePS.setText("Unnamed763");
        myName = namePS.getText();
        GenerationTableView();
        progressBar = ProgressStatus;

        if (myName.isEmpty()) {
            genStatistics.setDisable(true);
        }

        namePS.setOnKeyTyped(event -> genStatistics.setDisable(false));

        genStatistics.setOnAction(event -> {                                                                            // initialize of button 'genStatistics'
            Alert alertAddData = new Alert(Alert.AlertType.CONFIRMATION);
            alertAddData.setTitle("Attentions!");
            alertAddData.setHeaderText("Database is ready!");
            GeneratorDataBase.textArea.clear();
            JFileChooser fileopen = new JFileChooser();
            // fileopen.setMultiSelectionEnabled(true);
            fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileopen.setCurrentDirectory(new File("C:\\"));
            fileopen.setDialogTitle("Select Directory");
            int ret = fileopen.showDialog(null, "Select");
            if (ret == JFileChooser.APPROVE_OPTION) {
                List<File> newFiles = new ArrayList<>();
                File fileDirect = fileopen.getSelectedFile();
                List<Path> paths = listFiles(fileDirect);
                List<File> fileList = new ArrayList<>();
                paths.forEach(path1 -> fileList.add(new File(path1.toString())));
                try {
                    newFiles = GeneratorDataBase.CheckingTournaments(fileList, myName);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
                if (!newFiles.isEmpty()) {
                    try {
                        for (File file : newFiles) {
                         //   Preloader.ProgressNotification info = new Preloader.ProgressNotification((double) newFiles.indexOf(file)/(newFiles.size()-1));
                          //  LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification((double) newFiles.indexOf(file)/(newFiles.size()-1)));
                            final double ddd = (double) newFiles.indexOf(file)/(newFiles.size()-1);
                            progressBar.setProgress(ddd);

                           //    Thread.sleep(100);

                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            List<String> deals = new ArrayList<>();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (!line.equals("")) {
                                    deals.add(line);
                                }
                            }

                            for (String s : deals) {
                                words = s.split("\\s*(\\s|,|#|№|-|:)\\s*");

                                if (words[1].equals("Hand")) {
                                    indexHand.add(deals.indexOf(s));
                                    numberTournament = Long.parseLong(words[4]);
                                }
                            }
                            indexHand.add(deals.size() - 1);

                            matrixOfDeals.add(new MatrixOfDeals(numberTournament, GenerationStatistic(deals, indexHand)));

                            indexHand.clear();
                        }
                        GeneratorDataBase.AddStatToDataBase(matrixOfDeals, namesRowInMatrix, sumChips);
                        tableStatistics.setItems(GeneratorDataBase.ViewInTable());
                    } catch (IOException | SQLException ex) {
                        System.out.println(ex.getMessage());
                        System.out.println("Error");
                    }
                }

                GeneratorDataBase.textArea.setEditable(true);
                GeneratorDataBase.textArea.setWrapText(true);
                GeneratorDataBase.textArea.setMaxWidth(Double.MAX_VALUE);
                GeneratorDataBase.textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(GeneratorDataBase.textArea, Priority.ALWAYS);
                GridPane.setHgrow(GeneratorDataBase.textArea, Priority.ALWAYS);
                GridPane content = new GridPane();
                content.setMaxWidth(Double.MAX_VALUE);
                content.add(GeneratorDataBase.textArea, 0, 0);

                alertAddData.getDialogPane().setContent(content);
                alertAddData.showAndWait();
            }

        });

        clearDataBase.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Clear DataBase?");
            alert.setTitle("Warning");
            alert.setHeaderText("Attention!");



            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                try {
                    GeneratorDataBase.ClearDB();
                    tableStatistics.getItems().clear();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    public void start(Stage primaryStage) {

    }

}

