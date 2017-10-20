package soundtracker;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public Label searchMusic;
    public DirectoryChooser chooser;
    private List<String> fileNames;
    private Stage stage;
    volatile String labelText;
    private enum PathSeparator
    {
        SLASH( "/" ),
        BACKSLASH( "\\" );

        private String value;

        PathSeparator( final String value )
        {
            this.value = value;
        }
        public String getValue()
        {
            return value;
        }
    }

    public void beginSearch(ActionEvent actionEvent)
    {
        chooser = new DirectoryChooser();
        chooser.setTitle( "Select root directory" );
        fileNames = new ArrayList<String>();
        File rootDirectory = chooser.showDialog( stage.getScene().getWindow() );
        if ( rootDirectory != null )
        {
            String fileName = rootDirectory.getAbsolutePath() + PathSeparator.BACKSLASH.getValue() + "library.txt";
            Path file = Paths.get(fileName);
            long startSearch = System.currentTimeMillis();

            Label wait = (Label) stage.getScene().lookup("#wait");
            wait.setText("Search in progress!");
            wait.setVisible(true);

            Task <Void> task = new Task<Void>() {
                @Override public Void call() throws InterruptedException
                {
                    fileNames.add(rootDirectory.getAbsolutePath());
                    Controller.this.searchDirectory(rootDirectory);
                    long stopSearch = System.currentTimeMillis();
                    try
                    {
                        Files.write(file, fileNames, Charset.forName("UTF-8"));

                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    long stopWrite = System.currentTimeMillis();
                    System.out.println("Search duration: " + ((stopSearch - startSearch) / 1000));
                    System.out.println("Full duration: " + ((stopWrite - startSearch) / 1000));
                    labelText = "Search completed!";
                    return null;
                }
            };
            searchMusic.textProperty().bind(task.messageProperty());
            task.setOnSucceeded(e -> {
                searchMusic.textProperty().unbind();
                searchMusic.setText(labelText);
            });
            Thread thread = new Thread(task);
            thread.start();
        }
        else
        {
            searchMusic.setText( "Search failed!" );
        }

    }

    private void searchDirectory(File file)
    {
        fileNames.add("--------------------");
        if ( file.listFiles() != null)
        {
            for (File f : file.listFiles() )
            {
                if (f.isDirectory())
                {
                    searchDirectory(f);
                }
                else if (f.isFile())
                {
                    if (f.getName().contains(".mp3") || f.getName().contains(".wav") || f.getName().contains(".aif")
                        || f.getName().contains(".cda") || f.getName().contains(".mid") || f.getName().contains(".midi")
                        || f.getName().contains(".mpa") || f.getName().contains(".ogg") || f.getName().contains(".wma")
                        || f.getName().contains(".wpl"))
                    {
                        fileNames.add(f.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
}
