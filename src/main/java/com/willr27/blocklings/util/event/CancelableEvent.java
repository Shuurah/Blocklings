package com.willr27.blocklings.util.event;

/**
 * A cancellable event.
 */
public class CancelableEvent extends Event
{
    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled = false;

    /**
     * @return whether the event is cancelled.
     */
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    /**
     * Sets whether the event is cancelled.
     */
    public void setIsCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;

        setIsHandled(true);
    }
}
