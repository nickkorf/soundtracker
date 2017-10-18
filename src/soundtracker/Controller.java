package soundtracker;

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
    public enum PathSeparator
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
            String fileName = rootDirectory.getAbsolutePath() + PathSeparator.BACKSLASH.getValue() + "output.txt";
            Path file = Paths.get(fileName);
            searchDirectory(rootDirectory);

            try
            {
                Files.write( file, fileNames, Charset.forName( "UTF-8" ) );
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            searchMusic.setText( "Search completed!" );
        }
        else
        {
            searchMusic.setText( "Search failed!" );
        }

    }

    private void searchDirectory(File file)
    {
        String fileName = file.getAbsolutePath();
        fileNames.add(fileName);
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
                    fileNames.add(f.getAbsolutePath());
                }
            }
        }
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
}
