/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.reducers.bucket;

import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.aggregations.bucket.BucketStreamContext;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.reducers.Reducer;
import org.elasticsearch.search.reducers.ReducerContext;
import org.elasticsearch.search.reducers.ReducerFactories;
import org.elasticsearch.search.reducers.ReductionExecutionException;
import org.elasticsearch.search.reducers.bucket.InternalBucketReducerAggregation.InternalSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BucketReducer extends Reducer {


    public BucketReducer(String name, String bucketsPath, ReducerFactories factories, ReducerContext context, Reducer parent) {
        super(name, bucketsPath, factories, context, parent);
    }

    public BucketReducer(String name, List<String> bucketsPaths, ReducerFactories factories, ReducerContext context, Reducer parent) {
        super(name, bucketsPaths, factories, context, parent);
    }

    @Override
    public abstract InternalBucketReducerAggregation doReduce(List<MultiBucketsAggregation> aggregations, BytesReference bucketType,
            BucketStreamContext bucketStreamContext) throws ReductionExecutionException;

    protected InternalAggregations runSubReducers(InternalSelection selection) {
        Reducer[] subReducers = subReducers();
        List<InternalAggregation> aggregations = new ArrayList<>(subReducers.length);
        for (Reducer reducer : subReducers) {
            aggregations.add(reducer.doReduce(Collections.singletonList((MultiBucketsAggregation) selection), selection.getBucketType(),
                    selection.getBucketStreamContext()));
        }
        return new InternalAggregations(aggregations);
    }
}