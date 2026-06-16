package dev.willyelton.crystal.tools.common.inventory;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Resource handler that delegates operations to the given input or output resource handlers.
 * The priority is based off of the order they are passed in.
 * @param <T> A resource
 */
public class InputOutputDelegatingResourceHandler<T extends Resource> implements ResourceHandler<T> {
    private final List<ResourceHandler<T>> inputHandlers;
    private final List<ResourceHandler<T>> outputHandlers;

    public InputOutputDelegatingResourceHandler(List<ResourceHandler<T>> inputHandlers, List<ResourceHandler<T>> outputHandlers) {
        this.inputHandlers = inputHandlers;
        this.outputHandlers = outputHandlers;
    }

    protected Pair<ResourceHandler<T>, Integer> getHandlerFromIndex(int index) {
        int size = size();
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out-of-bounds for input output handler with size " + size);
        }

        int i = 0;
        for (ResourceHandler<T> handler : inputHandlers) {
            int handlerSize = handler.size();
            if (index - i < handlerSize) {
                return Pair.of(handler, index - i);
            }
            i += handlerSize;
        }

        for (ResourceHandler<T> handler : outputHandlers) {
            int handlerSize = handler.size();
            if (index - i < handlerSize) {
                return Pair.of(handler, index - i);
            }
            i += handlerSize;
        }

        throw new IndexOutOfBoundsException("This shouldn't get here?");
    }

    @Override
    public int size() {
        return inputHandlers.stream().mapToInt(ResourceHandler::size).sum() +
                outputHandlers.stream().mapToInt(ResourceHandler::size).sum();
    }

    // 5  4  2
    // 4 5
    // size = 20
    @Override
    public T getResource(int index) {
        var handler = getHandlerFromIndex(index);
        return handler.getLeft().getResource(handler.getRight());
    }

    @Override
    public long getAmountAsLong(int index) {
        var handler = getHandlerFromIndex(index);
        return handler.getLeft().getAmountAsLong(handler.getRight());
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        var handler = getHandlerFromIndex(index);
        return handler.getLeft().getCapacityAsLong(handler.getRight(), resource);
    }

    @Override
    public boolean isValid(int index, T resource) {
        var handler = getHandlerFromIndex(index);
        return handler.getLeft().isValid(handler.getRight(), resource);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        var handler = getHandlerFromIndex(index);
        return handler.getLeft().insert(handler.getRight(), resource, amount, transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        var handler = getHandlerFromIndex(index);
        return handler.getLeft().extract(handler.getRight(), resource, amount, transaction);
    }

    @Override
    public int insert(T resource, int amount, TransactionContext transaction) {
        int amountLeft = amount;
        int inserted = 0;
        for (ResourceHandler<T> handler : inputHandlers) {
            int handlerInserted = handler.insert(resource, amountLeft, transaction);
            amountLeft -= handlerInserted;
            inserted += handlerInserted;
        }

        return inserted;
    }

    @Override
    public int extract(T resource, int amount, TransactionContext transaction) {
        int amountLeft = amount;
        int extracted = 0;
        for (ResourceHandler<T> handler : outputHandlers) {
            int handlerExtracted = handler.extract(resource, amountLeft, transaction);
            amountLeft -= handlerExtracted;
            extracted += handlerExtracted;
        }

        return extracted;
    }
}
