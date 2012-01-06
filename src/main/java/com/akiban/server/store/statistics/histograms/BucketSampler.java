/**
 * Copyright (C) 2011 Akiban Technologies Inc.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package com.akiban.server.store.statistics.histograms;

import com.akiban.util.Flywheel;
import com.akiban.util.Recycler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

final class BucketSampler<T> {
    
    void add(Bucket<T> bucket, Flywheel<Bucket<T>> releaseTo) {
        BucketNode<T> node = nodeFor(bucket);
        node.prev = last;
        last.next = node;
        last = node;
        if (++size > maxSize) { // need to trim
            BucketNode<T> removeNode = nodeToRemove();
            assert removeNode.next != null : removeNode;
            Bucket<T> removeBucket = removeNode.bucket;
            releaseTo.recycle(removeBucket);

            Bucket<T> foldIntoBucket = removeNode.next.bucket;
            assert foldIntoBucket != null;
            foldIntoBucket.addLessThans(removeBucket.getEqualsCount() + removeBucket.getLessThanCount());
            foldIntoBucket.addLessThanDistincts(removeBucket.getLessThanDistinctsCount() + 1);
            // update the removeNode's prev and next to point to each other
            removeNode.prev.next = removeNode.next;
            if (removeNode.next != null)
                removeNode.next.prev = removeNode.prev;
            --size;
            checkIntegrity();
            bucketNodeReserves.recycle(removeNode);
            recycler.recycle(removeBucket.value());
        }
        else {
            addPenultToBucketNodeSets();
        }
    }

    List<Bucket<T>> buckets() {
        List<Bucket<T>> results = new ArrayList<Bucket<T>>(size);
        for(BucketNode<T> node = sentinel.next; node != null; node = node.next) {
            results.add(node.bucket);
        }
        return results;
    }

    private void checkIntegrity() {
        BucketNode<T> last = null;
        for(BucketNode<T> node = sentinel; node != null; node = node.next) {
            if (node.prev != last)
                System.out.printf("expected node.prev=%s but was %s%n", last, node.prev);
            last = node;
        }
    }

    BucketSampler(int maxSize, Random usingRandom, Recycler<? super T> recycler) {
        if (maxSize < 2)
            throw new IllegalArgumentException("max must be at least 2");
        this.maxSize = maxSize;
        this.sentinel = new BucketNode<T>();
        this.last = sentinel;
        this.bucketNodeSets = new TreeMap<Long, BucketNodeSet<T>>();
        this.random = usingRandom;
        this.recycler = recycler;
    }
    
    protected long popularity(Bucket<T> bucket) {
        return bucket.getEqualsCount();
    }

    private BucketNode<T> nodeToRemove() {
        BucketNode<T> penult = last.prev;
        long lastNodePopularity = popularity(penult.bucket);
        Map.Entry<Long,BucketNodeSet<T>> leastPopularEntry = bucketNodeSets.firstEntry();
        long leastPopular = leastPopularEntry.getKey();
        if (lastNodePopularity < leastPopular) {
            return penult;
        }
        if (lastNodePopularity == leastPopular) {
            BucketNodeSet<T> bucketNodeSet = leastPopularEntry.getValue();
            if (bucketNodeSet.totalSeen > bucketNodeSet.bucketNodes.size()) {
                // if there have been T buckets seen, and there are S buckets now, then
                // T-S buckets have been removed. That means each bucket has had a (T-S)/T chance of being removed.
                // Another way of looking at it: there have been T total, and there are S now. So each nosw in here has
                // had an S/T chance of being picked, which means a 1 - (S/T) chance of being removed.
                //     1 - (S/T) = T/T - S/T = (T-S)/T
                // So, we need to give this guy the same opportunity!
                int numer = bucketNodeSet.totalSeen - bucketNodeSet.bucketNodes.size();
                int denom = bucketNodeSet.totalSeen + 1;
                if (rand(denom) < numer) {
                    bucketNodeSet.totalSeen++;
                    return penult;
                }
                else {
                    bucketNodeSet.add(penult);
                }
            }
            else {
                bucketNodeSet.add(penult);
            }
        }
        else {
            // not the least popular! Add this to its entry, creating if needed
            addPenultToBucketNodeSets();
        }
        // now, clear a random one out of the least popular entry
        BucketNodeSet<T> bucketNodeSet = leastPopularEntry.getValue();
        final BucketNode<T> result;
        if (bucketNodeSet.bucketNodes.size() == 1) {
            // last element! get it and remove this node set from the map.
            // this is safe to do because the final results are guaranteed to have only
            // nodes which are more popular than this one.
            result = bucketNodeSet.bucketNodes.get(0);
            BucketNodeSet<T> removed = bucketNodeSets.remove(leastPopular);
            assert removed != null;
            bucketNodeSetReserves.recycle(removed);
        }
        else {
            List<BucketNode<T>> list = bucketNodeSet.bucketNodes;
            result = list.remove(rand(list.size()));
            bucketNodeSet.battles++;
        }
        return result;
    }

    private void addPenultToBucketNodeSets() {
        if (last.prev != sentinel)
            addToBucketNodeSets(last.prev);
    }

    private void addToBucketNodeSets(BucketNode<T> node) {
        long nodePopularity = popularity(node.bucket);
        BucketNodeSet<T> bucketNodeSet = bucketNodeSets.get(nodePopularity);
        if (bucketNodeSet == null) {
            if (bucketNodeSetReserves == null) {
                bucketNodeSetReserves = new Flywheel<BucketNodeSet<T>>() {
                    @Override
                    protected BucketNodeSet<T> createNew() {
                        return new BucketNodeSet<T>();
                    }
                };
            }
            bucketNodeSet = bucketNodeSetReserves.get();
            bucketNodeSet.init();
            bucketNodeSets.put(nodePopularity, bucketNodeSet);
        }
        bucketNodeSet.add(node);
    }

    private BucketNode<T> nodeFor(Bucket<T> bucket) {
        if (bucketNodeReserves == null) {
            bucketNodeReserves = new Flywheel<BucketNode<T>>() {
                @Override
                protected BucketNode<T> createNew() {
                    return new BucketNode<T>();
                }
            };
        }
        BucketNode<T> result = bucketNodeReserves.get();
        result.init(bucket);
        return result;
    }
    
    private int rand(int n) {
        if (random == null)
            random = new Random(System.nanoTime());
        return random.nextInt(n);
    }

    private final int maxSize;
    private int size;
    private final BucketNode<T> sentinel;
    private final NavigableMap<Long,BucketNodeSet<T>> bucketNodeSets;
    private Random random;
    private BucketNode<T> last;
    private final Recycler<? super T> recycler;
    private Flywheel<BucketNode<T>> bucketNodeReserves;
    private Flywheel<BucketNodeSet<T>> bucketNodeSetReserves;
    
    private static class BucketNode<A> {

        @Override
        public String toString() {
            return (prev==null) ? "SENTINAL" : String.valueOf(bucket);
        }

        public void init(Bucket<A> bucket) {
            this.bucket = bucket;
            this.next = null;
            this.prev = null;
        }

        Bucket<A> bucket;
        BucketNode<A> next;
        BucketNode<A> prev;
    }
    
    private static class BucketNodeSet<T> {

        @Override
        public String toString() {
            return String.format("%d buckets (%d total), %d battles", bucketNodes.size(), totalSeen, battles);
        }

        public void add(BucketNode<T> node) {
            bucketNodes.add(node);
            ++totalSeen;
        }

        public void init() {
            bucketNodes.clear();
            totalSeen = 0;
            battles = 0;
        }
        
        private final List<BucketNode<T>> bucketNodes = new ArrayList<BucketNode<T>>();
        private int totalSeen = 0;
        private int battles = 0;
    }
}