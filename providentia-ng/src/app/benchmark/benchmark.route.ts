import { Route } from '@angular/router';

import { BenchmarkComponent } from './benchmark.component';

export const BENCHMARK_ROUTE: Route = {
  path: 'benchmark/:id',
  component: BenchmarkComponent,
  data: {
    authorities: [],
    pageTitle: 'Benchmark - Providentia'
  }
};
