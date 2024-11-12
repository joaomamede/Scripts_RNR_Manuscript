/**
 * Remove detections that have ROIs that touch the border of any annotation ROI in QuPath v0.2.
 *
 * Note that there are some non-obvious subtleties involved depending upon how ROIs are accessed -
 * see the 'useHierarchyRule' option for more info.
 *
 * Written for https://forum.image.sc/t/remove-detected-objects-touching-annotations-border/49053
 *
 * @author Pete Bankhead
 */


import org.locationtech.jts.geom.util.LinearComponentExtracter
import qupath.lib.objects.PathDetectionObject
import qupath.lib.objects.PathObject
import qupath.lib.regions.ImageRegion

import java.util.stream.Collectors

import static qupath.lib.gui.scripting.QPEx.*

// Define the distance in pixels from an annotation boundary
// Zero is a valid option for 'touching'
double distancePixels = 0.0

// Toggle whether to use the 'hierarchy' rule, i.e. only consider detections with centroids inside an annotation
boolean useHierarchyRule = true


// Get parent annotations
def hierarchy = getCurrentHierarchy()
def annotations = hierarchy.getAnnotationObjects()

// Loop through detections
def toRemove = new HashSet<PathObject>()
for (def annotation in annotations) {
    def roi = annotation.getROI()
    if (roi == null)
        continue // Shouldn't actually happen...
    Collection<? extends PathObject> detections
    if (useHierarchyRule)
        // Warning! This decides based upon centroids (the 'normal' hierarchy rule)
        detections = hierarchy.getObjectsForRegion(PathDetectionObject.class, ImageRegion.createInstance(roi), null)
    else
        // This uses bounding boxes (the 'normal' hierarchy rule)
        detections = hierarchy.getObjectsForROI(PathDetectionObject.class, roi)
    // We need to get separate line strings for each polygon (since otherwise we get distances of zero when inside)
    def geometry = roi.getGeometry()
    for (def line in LinearComponentExtracter.getLines(geometry)) {
        toRemove.addAll(
                detections.parallelStream()
                        .filter(d -> line.isWithinDistance(d.getROI().getGeometry(), distancePixels))
                        .collect(Collectors.toList())
        )
    }
}
println "Removing ${toRemove.size()} detections without ${distancePixels} pixels of an annotation boundary"
hierarchy.removeObjects(toRemove, true)