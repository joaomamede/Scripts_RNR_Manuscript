///////////////////////////////////////////////////////////////////////////////////////////////////
/// USING CELLPOSE ALONE IN QUPATH -----
///////////////////////////////////////////////////////////////////////////////////////////////////


// get necessary modules! these can be downloaded to your computer
// just look up `qupath extension stardist` and `qupath extension cellpose` and
// https://github.com/qupath/qupath-extension-stardist
//
// FOLLOW THE INSTRUCTIONS listed on the github readme
// this version of cellpose requires access to your anaconda3 environment where cellpose is,
// which needs to be set in qupath (edit > preferences > cellpose)
import qupath.ext.stardist.StarDist2D
import qupath.ext.biop.cellpose.Cellpose2D
import qupath.lib.objects.PathObjects
import qupath.lib.analysis.features.ObjectMeasurements

// get rid of any current detections
clearDetections()

// Set some variables
var imageData = getCurrentImageData()
var server = getCurrentServer()

createFullImageAnnotation(true); // This will create a full image annotation
//SelectAllObject(true); // This will create an annotation on whole image
//selectAnnotations(); // This will select the annotation already present in the image
//selectObjectsByClassification("Tissue_ignore"); // Specify the annotation that you want to use.

//resetSelection() // deselect any objects before continuing 

//selectObjectsByClassification("Tissue_ignore"); // select objects by their CLASSIFICATION (tumor, tissue, etc) [not by name]
                                          // can add one or more category names as strings separated by commas

var pathObjects = getSelectedObjects()
var cal = server.getPixelCalibration()
var downsample = 1.0

///////////////////////////////////////////////////////////////////////////////////////////////////
/// RUN CELLPOSE -----
///////////////////////////////////////////////////////////////////////////////////////////////////

pathModel = 'cyto2'
def cellpose = Cellpose2D.builder(pathModel)
        .channels('Nuclei') // THIS IS THE CYTOPLASM DETECTION CHANNEL
        .normalizePercentiles(1,99)
        .pixelSize( 0.325 )
        .diameter(25)
        .measureShape()
        .measureIntensity()
//        .useOmnipose()
        .cellExpansion(-2)
        .build()
cellpose.detectObjects(imageData, pathObjects)

// Save Cells
def Cells = getDetectionObjects()
selectDetections()

fireHierarchyUpdate()

//In order to delete cells by class with certain values
// Delete too small cells
def toDeleteTooSmall = getDetectionObjects().findAll {measurement(it, 'Area µm^2') < 40}
removeObjects(toDeleteTooSmall, true)

println 'Cells are filtered according to the cutoffs provided'

println 'Done!'