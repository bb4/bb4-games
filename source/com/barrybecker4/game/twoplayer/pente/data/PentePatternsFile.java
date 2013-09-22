/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente.data;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.pente.pattern.PentePatterns;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;


/**
 *  Encapsulates the domain knowledge for Pente.
 *  Its primary client is the PenteController class.
 *  These are key patterns that can occur in the game and are weighted
 *  by importance to let the computer play better.
 *
 *  @author Barry Becker
 */
public class PentePatternsFile extends PentePatterns {

    /**
     * This vector contains the key patterns to match
     * it is loaded from the PATTERN_FILE. (if used)
     */
    private String[] patterns_ = null;

    public PentePatternsFile() {

        // since reading files is not easy with applets, I've moved the pattern data into the class PentePatternsFile.
        // only use this method if you need to read the data from a file.
        readPatternFile();
        // only use this when changing the format
        //writePatternFile();
    }

    @Override
    protected int getNumPatterns() {
        return patterns_.length;
    }

    @Override
    protected String getPatternString(int i) {
        return patterns_[i];
    }

     protected String getPatternFile() {
        return GameContext.GAME_RESOURCE_ROOT + "pente/Pente.patterns1.dat";
    }

    protected String getExportFile() {
        return GameContext.GAME_RESOURCE_ROOT + "pente/Pente.export.dat";
    }


    /**
     * the pattern file is fixed for pente
     * this method fills in pattern_ and weightTable_
     */
    protected void readPatternFile() {
        // Open a file of the given name.
        String patternFileName = getPatternFile();
        File file = new File( patternFileName );
        FileInputStream patternFile = null;
        int token;
        int wtIndex = 0;

        try {
            patternFile = new FileInputStream( file );
        } catch (FileNotFoundException e) {
            GameContext.log(0, "file " + patternFileName  + " not found." + e.getMessage() );
        }
        InputStreamReader iStreamReader = new InputStreamReader( patternFile );
        BufferedReader inData = new BufferedReader( iStreamReader );
        StreamTokenizer inStream = new StreamTokenizer( inData );
        inStream.commentChar( '#' );
        inStream.slashSlashComments( true );
        inStream.wordChars( '_', '_' + 1 );

        try {
            // the first entry in the file should be the number of patterns
            //token = inStream.nextToken();
            int numPatterns = (int) (inStream.nval);
            patterns_ = new String[numPatterns];
            String pattern;
            for ( int i = 0; i < numPatterns; i++ ) {
                token = inStream.nextToken(); // must be TT_WORD
                if ( token == StreamTokenizer.TT_WORD ) {
                    patterns_[i] = inStream.sval;
                }
                else
                    GameContext.log(0,  "unexpected token type = " + token + "   nval = " + inStream.nval );
                token = inStream.nextToken(); // must be TT_NUMBER
                if ( token == StreamTokenizer.TT_NUMBER ) {
                    wtIndex = (int) (inStream.nval);
                }
                else
                    GameContext.log(0, "unexpected token type = " + token + "   sval = " + inStream.sval );

                pattern = patterns_[i];
                setPatternWeightInTable( pattern, wtIndex );
            }
            iStreamReader.close();
        } catch (IOException e) {
            GameContext.log(0,  "error occurred while reading " + patternFileName );
        }

    }

    /**
     * allow exporting the patterns and weight indices in a different format.
     * ordinarily we do not export the patterns, but sometimes we might want to
     * change the format.
     */
    protected void writePatternFile() {

        String exportFile = getExportFile();
        File file = new File( exportFile );
        FileOutputStream patternFile;

        try {
            patternFile = new FileOutputStream( file );
        } catch (FileNotFoundException e) {
            GameContext.log(0, "can't open " + exportFile + " for write" );
            e.printStackTrace();
            return;
        }
        OutputStreamWriter oStreamWriter = new OutputStreamWriter( patternFile );
        BufferedWriter outData = new BufferedWriter( oStreamWriter );
        int i;

        try {

            int numPatterns = getNumPatterns();
            GameContext.log(0,"there are " + numPatterns + " patterns. " );
            for ( i = 0; i < numPatterns; i++ ) {
                outData.write( '\"' + patterns_[i] + "\", " );
            }
            outData.write( "\r\n" );
            for ( i = 0; i < numPatterns; i++ ) {
                int index = getWeightIndexForPattern(patterns_[i]);
                outData.write( index + ", " );
            }
            outData.write( "\r\n" );
            outData.flush();
            patternFile.flush();
            outData.close();
            patternFile.close();
        } catch (IOException e) {
            GameContext.log(0, "error occurred while writing " + exportFile );
            e.printStackTrace();
        }
    }
}
