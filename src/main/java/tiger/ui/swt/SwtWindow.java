package tiger.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import tiger.animation.Animator;
import tiger.core.GlslProgramFloatParameter;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

import com.jogamp.opengl.GLEventListener;

public class SwtWindow {
    private String name;
    private int width;
    private int height;
    private GLEventListener tigerGlListener;
    private Display display;
    private Shell shell;
    public SwtCanvas canvas;
    private ScrolledComposite scrolledComposite;
    public Composite params;
    public TigerChangeListener redrawListener;

    public SwtWindow(String name, int width, int height, GLEventListener tigerGlListener) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.tigerGlListener = tigerGlListener;

        display = new Display();
        shell = new Shell(display);
        shell.setText(name);
        shell.setLayout(new GridLayout(2, false));
        shell.setSize(width, height);

        canvas = new SwtCanvas(shell, tigerGlListener);
        canvas.setLayoutData(new GridData(GridData.FILL_BOTH));

        scrolledComposite = new ScrolledComposite(shell, SWT.NONE | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        RowLayout panelRowLayout = new RowLayout(SWT.VERTICAL);
        panelRowLayout.wrap = false;
        params = new Composite(scrolledComposite, SWT.NONE);
        params.setLayout(panelRowLayout);

        redrawListener = new TigerChangeListener() {
            @Override
            public void stateChanged(TigerChangeEvent event) {
                canvas.glCanvas.redraw();
            }
        };
    }

    public void start() {
        start(null);
    }

    public void start(Animator animator) {
        params.pack();
        scrolledComposite.setContent(params);

        if(animator != null) {
            animator.addCanvas(canvas.glCanvas);
            animator.start();
        }
        else {
            shell.open();
            while(!shell.isDisposed()) {
                if(!display.readAndDispatch()) {
                   display.sleep();
                }
            }
        }

        canvas.dispose();
        display.dispose();
    }

    public void redrawCanvas() {
        canvas.glCanvas.redraw();
    }

    public void addFloatSlider(GlslProgramFloatParameter param, final float min, final float max) {
        final Label label = new Label(params, SWT.NONE);
        label.setText(param.name + " = " + param.getValue());

        final Scale scale = new Scale(params, SWT.HORIZONTAL);
        scale.setMinimum(0);
        scale.setMaximum(100);
        scale.setPageIncrement(5);
        scale.setSelection((int)(100f*(param.getValue()-min)/(max - min)));

        scale.addSelectionListener(new FloatSliderListener(param, min, max));
        param.addChangeListener(new TigerChangeListener<GlslProgramFloatParameter>() {
            @Override
            public void stateChanged(TigerChangeEvent<GlslProgramFloatParameter> event) {
                label.setText(param.name + " = " + param.getValue());
                GlslProgramFloatParameter p = event.getSource();
                int value = (int)(100f*(p.getValue()-min)/(max - min));
                if(value != scale.getSelection()) {
                    scale.setSelection(value);
                }
            }
        });
    }
}
