/**
 * Recipe for adding new measurements to QuPath (detection) objects.
 *
 * The specific calculation should be updated.
 * The one included here is based on this forum post https://forum.image.sc/t/calculate-a-new-feature-with-features-already-present/40883
 *
 * @author Pete Bankhead modified by Shoaib Arif (Mar 2022)
 */

import qupath.lib.objects.PathObject
import static qupath.lib.gui.scripting.QPEx.*

//Normalize the mean intensity by area of each cell
def calc = new Calculator()

// Get the objects (here, we use detections - change if required)
def pathObjects = getDetectionObjects()

// Add the measurements
pathObjects.each {
    try (def ml = it.getMeasurementList()) {
        ml.putMeasurement(calc.getName(), calc.calculate(it))
    }
}
fireHierarchyUpdate()

/**
 * Class to define your new measurement
 */
class Calculator {

    /**
     * TODO: Fill in the measurement name
     * @return the measurement name
     */
    String getName() {
        return "Int_Mean/Area"
    }

    /**
     * TODO: Fill in the measurement calculation
     * @return the measurement value for the specified object
     */
    double calculate(PathObject pathObject) {
        double Int_Mean = measurement(pathObject, "SAMHD1: Mean")
	double Cell_Area = measurement(pathObject, "Area µm^2")

        return (Int_Mean / Cell_Area)
    }


}

// Usually a good idea to print something, so we know it finished
 print 'Done with creating new column with Int_Mean/Area'