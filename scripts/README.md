## Scripts
This folder contains all the script file necessary for the raspberry pi.
##### monitor-heart-rate
This python script is responsible for monitoring the heart rate using the sensor and write the current heart rate to a text file. Will continurously read the input and calculate the BPM using a library.
To run the script, use the command `python monitor-heart-rate.py`

##### bluetooth-server
This python script is responsible for creating a bluetooth service that will be accessible by the android device. The script will create a server and listen for commands given to it using characters. When the read data command is received, the script will read the latest reading of the heart rate from the text file and will send it to the requested android device.
To run the script, use the command, `python bluetooth-server.py`
