/**
 * 
 */
package edu.incense.android.session;

/**
 * @author mxpxgx
 *
 */
public interface SessionCompletionListener {
    public void completedSession(String sessionName, long activeTime);
}
