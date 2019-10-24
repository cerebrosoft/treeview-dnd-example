import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

public class DragAndScrollTreeView<T> extends TreeView<T> {

    private Timeline scrollTimeline = new Timeline();
    private double scrollVelocity = 0;
    private boolean dropped;

    // Higher SCROLL_SPEED value = slower scroll.
    private static final int SCROLL_SPEED = 100;
    private static final int SCROLL_DURATION = 20;


    public DragAndScrollTreeView(TreeItem<T> root) {
        super(root);

        setupScrolling();
    }


    private void setupScrolling () {
        scrollTimeline.setCycleCount(Timeline.INDEFINITE);
        scrollTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(SCROLL_DURATION), (ActionEvent) -> dragScroll()));

        setOnDragExited((DragEvent event) -> {

            if (event.getY() > 0) {
                scrollVelocity = 1.0 / SCROLL_SPEED;
            }
            else {
                scrollVelocity = -1.0 / SCROLL_SPEED;
            }
            if (!dropped){
                scrollTimeline.play();
            }

        });

        setOnDragEntered(event -> {
            scrollTimeline.stop();
            dropped = false;
        });

        setOnDragDone(event -> {
            scrollTimeline.stop();
        });

        setOnDragDropped((DragEvent event) ->{
            scrollTimeline.stop();
            event.setDropCompleted(true);
            dropped = true;
        });

        setOnScroll((ScrollEvent event)-> {
            scrollTimeline.stop();
        });
    }


    private void dragScroll () {
        ScrollBar sb = getVerticalScrollbar();
        if (sb != null) {
            double newValue = sb.getValue() + scrollVelocity;
            newValue = Math.min(newValue, 1.0);
            newValue = Math.max(newValue, 0.0);
            sb.setValue(newValue);
        }
    }


    private ScrollBar getVerticalScrollbar() {
        ScrollBar result = null;
        for (Node n : lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }
}
