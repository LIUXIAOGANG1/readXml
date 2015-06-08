package net.sourceforge.jpowergraph.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.GraphListener;
import net.sourceforge.jpowergraph.Node;

/**
 * The layouter applies the layout strategy to the graph in a separate thread,
 */
public class Layouter {
    /** The graph that this layouter takes care of. */
    protected Graph m_graph;
    /** The layout strategy for this layouter. */
    protected LayoutStrategy m_layoutStrategy;
    /** The thread of the layouter. */
    protected LayouterThread m_layouterThread;
    private List <IterationListener> listeners;

    /**
     * Creates the layouter.
     *
     * @param layoutStrategy            the layout strategy
     */
    public Layouter(LayoutStrategy layoutStrategy) {
        m_layoutStrategy=layoutStrategy;
        m_graph=m_layoutStrategy.getGraph();
        m_graph.addGraphListener(new GraphHandler());
        listeners = new ArrayList <IterationListener> ();
    }
    /**
     * Starts the layouter thread.
     */
    public synchronized void start() {
        if (m_layouterThread==null) {
            m_layouterThread=new LayouterThread();
            m_layouterThread.start();
        }
    }
    /**
     * Stops the layouter thread.
     */
    public void stop() {
        LayouterThread layouterThread;
        synchronized (this) {
            layouterThread=m_layouterThread;
            m_layouterThread=null;
        }
        if (layouterThread!=null) {
            layouterThread.markStop();
            layouterThread.interrupt();
            try {
                layouterThread.join();
            }
            catch (InterruptedException ignored) {
            }
        }
    }
    
    public void addIterationListener(IterationListener theIterationListener){
        listeners.add(theIterationListener);
    }
    
    public void fireIterationListeners(){
        for (IterationListener i : listeners){
            i.iterationComplete();
        }
    }
    
    /**
     * The layouter thread.
     */
    protected class LayouterThread extends Thread {
        
        private int step = 0;
        private boolean stopped = false;
        
        public LayouterThread() {
            super("GraphLayouter");
            setDaemon(true);
        }
        public void markStop() {
            stopped = true;
            
        }
        public void run() {
            synchronized (m_graph) {
                m_layoutStrategy.graphContentsChanged();
                m_layoutStrategy.notifyGraphLayoutUpdated();
            }
            while (!stopped) {
                try {
                    boolean hasMoreSteps=false;
                    int skip = 3;
                    synchronized (m_graph) {
                        hasMoreSteps=m_layoutStrategy.shouldExecuteStep();
                        if (hasMoreSteps) {
                            m_layoutStrategy.executeGraphLayoutStep();
                            if (step == 0 || step % skip == 0){
                                m_graph.notifyLayoutUpdated();
                            }
                            step++;
                        }
                        else{
                            m_graph.notifyLayoutUpdated();
                            Layouter.this.fireIterationListeners();
                        }
                    }
                    if (hasMoreSteps)
                        Thread.sleep(20);
                    else
                        synchronized (Layouter.this) {
                            Layouter.this.wait();
                        }
                }
                catch (InterruptedException e) {
                    break;
                }
                catch (Throwable shouldntHappen) {
                    shouldntHappen.printStackTrace();
                }
            }
        }
    }

    /**
     * The handler for graph events.
     */
    protected class GraphHandler implements GraphListener {
        public void graphLayoutUpdated(Graph graph) {
            synchronized (m_graph) {
                synchronized (Layouter.this) {
                    if (m_layouterThread!=Thread.currentThread()) {
                        m_layoutStrategy.notifyGraphLayoutUpdated();
                        Layouter.this.notifyAll();
                    }
                }
            }
        }
        public void graphUpdated(Graph graph) {
        }
        public void graphContentsChanged(Graph graph) {
            synchronized (m_graph) {
                m_layoutStrategy.graphContentsChanged();
                notifyGraphChanged();
            }
        }
        public void elementsAdded(Graph graph,Collection <Node> nodes ,Collection <Edge> edges) {
            synchronized (m_graph) {
                m_layoutStrategy.elementsAdded(nodes,edges);
                notifyGraphChanged();
            }
        }
        public void elementsRemoved(Graph graph,Collection <Node> nodes,Collection <Edge> edges) {
            synchronized (m_graph) {
                m_layoutStrategy.elementsRemoved(nodes,edges);
                notifyGraphChanged();
            }
        }
        protected void notifyGraphChanged() {
            synchronized (Layouter.this) {
                m_layoutStrategy.notifyGraphLayoutUpdated();
                Layouter.this.notifyAll();
            }
        }
    }
}
