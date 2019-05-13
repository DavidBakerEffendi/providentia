import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { ProvidentiaTestModule } from '../../../test.module';
import { PrvMetricsMonitoringComponent } from 'app/admin/metrics/metrics.component';
import { PrvMetricsService } from 'app/admin/metrics/metrics.service';

describe('Component Tests', () => {
  describe('PrvMetricsMonitoringComponent', () => {
    let comp: PrvMetricsMonitoringComponent;
    let fixture: ComponentFixture<PrvMetricsMonitoringComponent>;
    let service: PrvMetricsService;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [ProvidentiaTestModule],
        declarations: [PrvMetricsMonitoringComponent]
      })
        .overrideTemplate(PrvMetricsMonitoringComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(PrvMetricsMonitoringComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PrvMetricsService);
    });

    describe('refresh', () => {
      it('should call refresh on init', () => {
        // GIVEN
        const response = {
          timers: {
            service: 'test',
            unrelatedKey: 'test'
          },
          gauges: {
            'jcache.statistics': {
              value: 2
            },
            unrelatedKey: 'test'
          }
        };
        spyOn(service, 'getMetrics').and.returnValue(of(response));

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(service.getMetrics).toHaveBeenCalled();
      });
    });
  });
});
