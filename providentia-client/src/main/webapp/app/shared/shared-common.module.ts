import { NgModule } from '@angular/core';

import { ProvidentiaSharedLibsModule, PrvAlertComponent, PrvAlertErrorComponent } from './';

@NgModule({
  imports: [ProvidentiaSharedLibsModule],
  declarations: [PrvAlertComponent, PrvAlertErrorComponent],
  exports: [ProvidentiaSharedLibsModule, PrvAlertComponent, PrvAlertErrorComponent]
})
export class ProvidentiaSharedCommonModule {}
