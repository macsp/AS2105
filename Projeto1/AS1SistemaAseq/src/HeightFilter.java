import java.text.SimpleDateFormat;
import java.util.Calendar;

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

public class HeightFilter extends FilterFramework
{


	public void run()
    {
        byte output;

        Calendar TimeStamp = Calendar.getInstance();
        SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");

        int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
        int IdLength = 4;				// This is the length of IDs in the byte stream

        byte databyte = 0;				// This is the data byte read from the stream
        int bytesread = 0;				// This is the number of bytes read from the stream

        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int id;							// This is the measurement id
        int i;							// This is a loop counter

        int byteswritten = 0;				// Number of bytes written to the stream.

		// Next we write a message to the terminal to let the world know we are alive...

		System.out.println(this.getName() + "::Height Reading ");

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

                    bytesread++;						// Increment the byte count

                    WriteFilterOutputPort(databyte);
                    byteswritten++;
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
                        // measurement
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
                 // Here we pick up a measurement (ID = 4 in this case), but you can pick up
                 // any measurement you want to. All measurements in the stream are
                 // decommutated by this class. Note that all data measurements are double types
                 // This illustrates how to convert the bits read from the stream into a double
                 // type. Its pretty simple using Double.longBitsToDouble(long value). So here
                 // we print the time stamp and the data associated with the ID we are interested
                 // in.
                 ****************************************************************************/
//                if is height
                if ( id == 2 )
                {
//                    System.out.println("input " + Long.toBinaryString(measurement));
//                    System.out.println(TimeStampFormat.format(TimeStamp.getTime()) + " ID = " + id + " Feet " + longToDouble(measurement));

//                  Convert to meters
                    double meters = Double.longBitsToDouble(measurement)/3.2808;

//                    System.out.println(TimeStampFormat.format(TimeStamp.getTime()) + " ID = " + id + " Meters " + meters);
                    measurement = doubleToLong(meters);
                    for(i = 0; i < 8; i++)
                    {
                        output = (byte)((measurement >> ((7 - i) * 8)) & 0xff);
                        WriteFilterOutputPort(output);
                        byteswritten++;
                    }
                } // if

//                else isn't height
                else
                {
                    for(i = 0; i < 8; i++)
                    {
                        output = (byte)((measurement >> ((7 - i) * 8)) & 0xff);
                        WriteFilterOutputPort(output);
                        byteswritten++;
                    }
                } // else
            } // try

			catch (EndOfStreamException e)
			{
                ClosePorts();
                System.out.println(this.getName() + "::Height Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
				break;
			} // catch
		} // while
   } // run

    Double longToDouble(long measurement)
    {
        return Double.longBitsToDouble(measurement);
    }

    long doubleToLong(double measurement)
    {
        return Double.doubleToLongBits(measurement);
    }

} // MiddleFilter