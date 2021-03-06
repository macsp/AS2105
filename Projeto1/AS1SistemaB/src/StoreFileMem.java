
import java.util.ArrayList;

/******************************************************************************************************************
 * File:MiddleFilter.java
 * Course: 17655
 * Project: Assignment 1
 * Copyright: Copyright (c) 2003 Carnegie Mellon University
 * Versions:
 *	1.0 November 2008 - Sample Pipe and Filter code (ajl).
 *
 * Description:
 *
 * This class serves as an example for how to use the FilterRemplate to create a standard filter. This particular
 * example is a simple "pass-through" filter that reads data from the filter's input port and writes data out the
 * filter's output port.
 *
 * Parameters: 		None
 *
 * Internal Methods: None
 *
 ******************************************************************************************************************/

public class StoreFileMem extends FilterFramework
{

    ArrayList<ListNode> lista = new ArrayList<ListNode>();
    int count = -1; //var to check highest ID
    byte output; //write to exit
    int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
    int IdLength = 4;
    int byteswritten = 0;				// Number of bytes written to the stream.

    public void run()
    {
        //Vars to save to list
        long temperature = 0;
        long height = 0;
        long pitch = 0;
        double pressure = 0;
        long speed = 0;
        long timestamp = 0;
        // This is the length of IDs in the byte stream

        byte databyte = 0;				// This is the data byte read from the stream
        int bytesread = 0;				// This is the number of bytes read from the stream

        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int id;							// This is the measurement id
        int i;							// This is a loop counter

        // Next we write a message to the terminal to let the world know we are alive...

        System.out.print( "\n" + this.getName() + "::StoreMem Reading ");

        while (true)
        {
            try
            {
                /***************************************************************************
                 // We know that the first data coming to this filter is going to be an ID and
                 // that it is IdLength long. So we first decommutate the ID bytes.
                 ****************************************************************************/

                id = 0;

                for (i=0; i<IdLength; i++ )
                {
                    databyte = ReadFilterInputPort();	// This is where we read the byte from the stream...

                    id = id | (databyte & 0xFF);		// We append the byte on to ID...

                    if (i != IdLength-1)				// If this is not the last byte, then slide the
                    {									// previously appended byte to the left by one byte
                        id = id << 8;					// to make room for the next byte we append to the ID

                    } // if
                    bytesread++;
                } // for

                /****************************************************************************
                 // Here we read measurements. All measurement data is read as a stream of bytes
                 // and stored as a long value. This permits us to do bitwise manipulation that
                 // is neccesary to convert the byte stream into data words. Note that bitwise
                 // manipulation is not permitted on any kind of floating point types in Java.
                 // If the id = 0 then this is a time value and is therefore a long value - no
                 // problem. However, if the id is something other than 0, then the bits in the
                 // long value is really of type double and we need to convert the value using
                 // Double.longBitsToDouble(long val) to do the conversion which is illustrated.
                 // below.
                 *****************************************************************************/

                measurement = 0;

                for (i=0; i<MeasurementLength; i++ )
                {
                    databyte = ReadFilterInputPort();
                    measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

                    if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
                    {												// previously appended byte to the left by one byte
                        measurement = measurement << 8;				// to make room for the next byte we append to the
                    } // if

                    bytesread++;									// Increment the byte count

                } // for

//				if ( id == 0 ) Tempo
//				if ( id == 1 ) Velocidade
//				if ( id == 2 ) Altitude
//				if ( id == 3 ) Pressão
//				if ( id == 4 ) Temperatura
//				if ( id == 5 ) Pitch

                /****************************************************************************
                 Create and fill node
                 ****************************************************************************/


                if ( id == 0){

                    if(count >= 0){
                        ListNode novo = new ListNode();
                        novo.setTime(timestamp);
                        novo.setTemperature(temperature);
                        novo.setHeight(height);
                        novo.setSpeed(speed);
                        novo.setPressure(pressure);
                        novo.setPitch(pitch);
                        lista.add(novo);
                    }

                    if(count == -1){
                        count++;
                    }
                    timestamp = measurement;
                }//if

                if ( id == 1 ) {
                    if(count < id){
                        count++;
                    }
                    speed = measurement;
                } // if

                if( id == 2){

                    if(count < id){
                        count++;
                    }
                    height = measurement;
                }

                if( id == 3){
                    if(count < id){
                        count++;
                    }
                    pressure = Double.longBitsToDouble(measurement);
                }

                if(id == 4){
                    if(count < id){
                        count++;
                    }
                    temperature =  measurement;
                }

                if(id == 5){
                    if(count < id){
                        count++;
                    }
                    pitch = measurement;
                }
            } // try

            catch (EndOfStreamException e)
            {
                //ClosePorts();
                System.out.print(this.getName() + "::StoreMem Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
                System.out.print("\n");
                break;
            } // catch

        } // while

        //last elem
        ListNode novo = new ListNode();
        novo.setTime(timestamp);
        novo.setTemperature(temperature);
        novo.setHeight(height);
        novo.setSpeed(speed);
        novo.setPressure(pressure);
        novo.setPitch(pitch);
        lista.add(novo);


        handleWildPoints();
        convertAndSend();
    } // run

    private void handleWildPoints() {

        int index_init = -1, index_final = -1;
        double pressureRevised; //pressao apos calculo da media


        for(int z = 0; z<lista.size();z++){

            if(lista.get(z).getPressure() < 50 || lista.get(z).getPressure() > 80){
                //encontra o valor mas não faz nada, reformular isto amanha

            }

            else{

                if(index_init == -1){
                    index_init = z;
                    //Se o primeiro valor certo nao for o primeiro do ficheiro tem que substituir os anteriores
                    if(index_init>0){
                        for(int j = 0; j<index_init;j++){
                            lista.get(j).setPressure(-lista.get(index_init).getPressure());
                        }
                    }
                }

                else{
                    index_final = z;
                    pressureRevised = (lista.get(index_init).getPressure() + lista.get(index_final).getPressure())/2;
                    for(int j = index_init+1; j < index_final;j++){

                        lista.get(j).setPressure(-pressureRevised);
                    }
                    index_init = index_final;
                    index_final = -1;
                }
            }
        }
        //No caso se so encontrar um unico ponto certo no file
        if(index_final == -1) {
            for (int z = index_init + 1; z < lista.size(); z++) {
                lista.get(z).setPressure(-lista.get(index_init).getPressure());
            }
        }

    }

    private void printList() {

        for(int z = 0; z<lista.size();z++) {
            System.out.println("Pressure = " + lista.get(z).getPressure());
        }
    }


    private void convertAndSend() {

        for(int z = 0; z<lista.size();z++) {

            sendId(0);
            sendMeasurement(lista.get(z).getTime());
            sendId(1);
            sendMeasurement(lista.get(z).getSpeed());
            sendId(2);
            sendMeasurement(lista.get(z).getHeight());
            sendId(3);
            sendMeasurement(Double.doubleToLongBits(lista.get(z).getPressure()));
            sendId(4);
            sendMeasurement(lista.get(z).getTemperature());
            sendId(5);
            sendMeasurement(lista.get(z).getPitch());

        }
    }

    private void sendMeasurement(long measure) {

        for (int a = 0; a < MeasurementLength; a++) {
            output = (byte) (measure >> ((7 - a) * 8) & 0xff);
            WriteFilterOutputPort(output);
            byteswritten++;
        }


    }

    private void sendId(int x) {

        for (int j = 0; j < IdLength; j++) {
            output = (byte) ((x >> ((7 - j) * 8)) & 0xff);
            WriteFilterOutputPort(output);
            byteswritten++;
        }

    }

    Double longToDouble(long measurement)
    {
        return Double.longBitsToDouble(measurement);
    }

    long doubleToLong(double measurement)
    {
        return Double.doubleToLongBits(measurement);
    }

} // MiddleFilter