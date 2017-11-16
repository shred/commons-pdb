/*
 * Shredzone Commons - pdb
 *
 * Copyright (C) 2009 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.shredzone.commons.pdb.appinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A standard {@link AppInfo} container that contains a list of category names.
 */
public class CategoryAppInfo implements AppInfo {

    private List<Category> categories = new ArrayList<>();

    /**
     * Gets a list of category names.
     */
    public List<Category> getCategories()           { return categories; }

    /**
     * Finds a {@link Category} by its index.
     *
     * @param index
     *            Category index
     * @return {@link Category} or {@code null}
     */
    public Category getCategoryByIndex(int index) {
        return categories.get(index);
    }

    /**
     * Finds a {@link Category} by its key.
     *
     * @param key
     *            Category key
     * @return {@link Category} or {@code null}
     */
    public Category getCategoryByKey(int key) {
        for (Category cat : categories) {
            if (cat != null && cat.getKey() == key) {
                return cat;
            }
        }
        return null;
    }

    /**
     * Finds a {@link Category} index by the category key.
     *
     * @param key
     *            Category key
     * @return Index of that category, or -1 if there was none with that key.
     */
    public int findCategoryByKey(int key) {
        for (int ix = 0; ix < categories.size(); ix++) {
            Category cat = categories.get(ix);
            if (cat != null && cat.getKey() == key) {
                return ix;
            }
        }

        return -1;
    }

    /**
     * Finds a {@link Category} index by the category name.
     *
     * @param name
     *            Category name
     * @return Index of that category, or -1 if there was none with that name.
     */
    public int findCategoryByName(String name) {
        for (int ix = 0; ix < categories.size(); ix++) {
            Category cat = categories.get(ix);
            if (cat != null && cat.getName().equals(name)) {
                return ix;
            }
        }

        return -1;
    }

    /**
     * A single category.
     */
    public static class Category {
        private final String name;
        private final int key;
        private final boolean renamed;

        public Category(String name, int key, boolean renamed) {
            this.name = name;
            this.key = key;
            this.renamed = renamed;
        }

        public String getName()                     { return name; }
        public int getKey()                         { return key; }
        public boolean isRenamed()                  { return renamed; }

        @Override
        public String toString() {
            return name + " (" + key + ")";
        }
    }

}
