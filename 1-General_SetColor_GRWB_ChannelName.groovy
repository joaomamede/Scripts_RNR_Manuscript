def viewer = getCurrentViewer()

    //Set the channel names
    setChannelNames(
     'Cy5_Blank',
     'SAMHD1',
     'FITC_Blank',
     'Nuclei'
     )

    //This function changed between 0.1.2 and 0.2.0, in 0.2.0 use viewer.getImageDisplay().availableChannels()
    def channels = viewer.getImageDisplay().availableChannels()

    // Set the LUT color for the first channel & repaint
    channels[1].setLUTColor(0, 255, 0) //green
    channels[0].setLUTColor(255, 0, 0) //red
    channels[2].setLUTColor(255, 255, 255) //white
    channels[3].setLUTColor(0, 0, 255) //blue
    

    // Ensure the updates are visible
    viewer.repaintEntireImage()
    

    // Usually a good idea to print something, so we know it finished
    print 'Done!'