package ede.stl.common;

import ede.stl.common.Position;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Source {

        private final Reader input;

        private int past;
        private int current;
        private int next;

        private int lineNumber;
        private int linePosition;

        private int lineStart;
        private int positionStart;

        public Source(InputStream inputStream) { this(new InputStreamReader(inputStream), 1, 1); }

        public Source(InputStream inputStream, int lineStart, int positionStart) { this(new InputStreamReader(inputStream), lineStart, positionStart); }

        public Source(Reader inputReader) { this(inputReader, 1, 1); }

        public Source(Reader inputReader, int lineStart, int positionStart) {
                input = inputReader;
                this.positionStart = positionStart;
                this.lineStart = lineStart;
                lineNumber = lineStart;
                linePosition = positionStart;

                try {
                   next = input.read();
                } catch (Exception e) {
                   System.err.println("Error could not read incoming character and could not advance");
                }

                if (!hasNext()) {
                     current = -1;
                     past = -1;
                } else {
                    current = -1;
                    advance();
                }

        }

        public void advance(){

            if (!atEOD() || hasNext()) {
                        past = current;
                        current = next;

                        if (current == '\n') {
                                lineNumber++;
                                linePosition = positionStart;
                        } else {
                                linePosition++;
                        }

                        try {
                                next = input.read();
                        } catch (Exception e) {
                                System.err.println("Error could not read incoming character and could not advance");
                        }
            }

        }

        public void advance(int times){ for (int i = 0; i < times; i++) { advance(); } }

        public char getPast(){ return (char)past; }

        public char getCurrent(){ return (char)current; }

        public char getNext(){ return (char)next; }

        public boolean hasNext(){ return next != -1; }

        public boolean atEOD(){ return current == -1; }

        public void close(){

                try {
                        input.close();
                } catch (Exception e) {
                        System.err.println("Error: could not close input stream/reader correctly");
                }

        }

        /**
         * Returns the position of the source in the stream
         * 
         * @return A Position object or tuple containing two elements the line position and the
         *         line number
         */

        public Position getPosition(){ return new Position(lineNumber, linePosition); }
}


























































