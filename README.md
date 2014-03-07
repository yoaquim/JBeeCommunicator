JBee Communicator
================

Java application that interfaces with an <i>XBee RF Module</i> - 802.15.4 protocol - which listens/writes on a serial port. Used to send/recieve text.


Intro
================

This app was developed to establish a communication channel between two XBees: one was connected to a PC running said app; the other, with an <b>MSP430G2553</b> microprocessor. It was purposedly designed to transfer text - anything else was never tested. Ran on Windows, so XBee serial connection was on COM port 11, tough you can set the port to whichever available one you want.

Once a connection is established, values are passed as "clearance keys" to make sure the counterpart was the matching XBee device; this is is optional. The implementation has a <b>very raw, incomplete, somewhat-buggy GUI</b>, which <b>I do not 
recommend</b>. The actual transfer parts should work fairly well, although you might need to fix a couple of things on your end (this project was part of a larger whole, and I stripped it of the non-essential parts). Documentation is scarce, but I'm currently working on that (XBees were disassembled; trying to get all the hardware back together to re-test).


Dependencies
================

The application uses the javacomm library, which is included under the <i>javaxcomm</i> folder. It should be noted that the <a href="https://code.google.com/p/java-simple-serial-connector/">jSSC</a> library might be a better fit if you are looking to use serial ports with Java.

It also relies on - for the GUI which I don't recommend - <a href="http://www.jgoodies.com/freeware/libraries/forms/">JGoodies Form Layout</a>.

You should definitely consult the XBee datasheet, which can be found online.


Important Caveat
================

You're free to use this however you want, but please be aware that it is not a finished application, nor is it as robust as it should be. I'm providing it "as is" and I've left much of the parameters that were originally used, to serve as examples. It was succesfully  used in a project, but was developed <i>on-the-fly</i>, and should serve more as a <b>learning resource</b> rather than some plug-and-play code; it's been a long time since  I worked on it, but given the scarcity of the topic online, I thought I'd share what little I had with the community. Feel free to help out, or contact me with updates, improvements or anything else you come up with.

