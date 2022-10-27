package dev.customitem.util;

import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * This utility is used to query the existence of
 * a set of {@link org.bukkit.Material}, and provide
 * method to consume the set if needed.
 *
 * @author Alphaharrius
 */
public class ItemConsumer {

    private final Inventory inventory;
    private final Map<Material, Integer> queriesMap;
    private final List<Object> executionList;

    private boolean consumable;

    /**
     * @param inventory {@link org.bukkit.inventory.Inventory} The inventory to be queried.
     * @param queries   {@link java.lang.Object} {@link Object ...} The query parameters, in format of {@code { MATERIAL, AMOUNT, MATERIAL, AMOUNT, ... }}
     * @throws java.security.InvalidParameterException This exception will be thrown when the {@code queries} is not of length of even numbers, or wth invalid format.
     */
    public ItemConsumer(@Nonnull Inventory inventory, Object ...queries) throws InvalidParameterException {

        this.inventory = inventory;
        this.queriesMap = new HashMap<>();
        this.executionList = new LinkedList<>();
        validateQuery(queries);
    }

    private void initializeQueriesMap(Object ...queries) throws InvalidParameterException {

        int querySize = queries.length / 2;

        for (int i = 0; i < querySize; i++) {

            Object materialObject = queries[i * 2];
            Object amountObject = queries[i * 2 + 1];

            if (!(materialObject instanceof Material) || !(amountObject instanceof Integer)) {

                throw new InvalidParameterException();
            }

            queriesMap.put((Material) materialObject, (int) amountObject);
        }
    }

    private void validateQuery(Object ...queries) throws InvalidParameterException {

        if (queries.length % 2 != 0) {

            throw new InvalidParameterException();
        }

        initializeQueriesMap(queries);

        for (ItemStack itemStack : inventory.getContents()) {

            // Individual item stack might be null as described
            // in "org.bukkit.inventory.Inventory".
            if (itemStack == null) { continue; }
            Material material = itemStack.getType();
            if (!queriesMap.containsKey(material)) { continue; }
            int consumingAmount = queriesMap.get(material);
            if (consumingAmount == 0) { continue; }
            int providedAmount = itemStack.getAmount();

            if (consumingAmount >= providedAmount) {

                consumingAmount -= providedAmount;
                executionList.add(itemStack);
                executionList.add(0);

            } else {

                executionList.add(itemStack);
                executionList.add(providedAmount - consumingAmount);
                consumingAmount = 0;
            }
            queriesMap.replace(material, consumingAmount);
        }
        for (int consumingAmount : queriesMap.values()) {

            if (consumingAmount != 0) {

                consumable = false;
                return;
            }
        }
        consumable = true;
    }

    /**
     * This method returns whether the current query is valid for consuming.
     * @return {@link java.lang.Boolean}:   Returns {@code true} when the given inventory contents
     *                                      satisfies the amount of the given materials,
     *                                      {@code false} if not.
     */
    public boolean isConsumable() { return consumable; }

    /**
     * Consumes the queried amount of materials in the current inventory.
     */
    public void consume() {

        if (!consumable) { return; }
        Iterator<Object> executionIterator = executionList.iterator();

        while (executionIterator.hasNext()) {

            Object itemStackObject = executionIterator.next();
            ((ItemStack) itemStackObject).setAmount((int) executionIterator.next());
        }
    }

}
