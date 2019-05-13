import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ProvidentiaSharedLibsModule, ProvidentiaSharedCommonModule, PrvLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [ProvidentiaSharedLibsModule, ProvidentiaSharedCommonModule],
  declarations: [PrvLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [PrvLoginModalComponent],
  exports: [ProvidentiaSharedCommonModule, PrvLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaSharedModule {
  static forRoot() {
    return {
      ngModule: ProvidentiaSharedModule
    };
  }
}
