import {
    MatButtonModule,
    MatCheckboxModule,
    MatToolbarModule,
    MatTabsModule,
    MatSidenavModule,
    MatListModule,
    MatCardModule,
    MatGridListModule,
} from '@angular/material';
import {
    NavbarModule,
    WavesModule,
    ButtonsModule
} from 'angular-bootstrap-md';
import { NgModule } from '@angular/core';

@NgModule({
    imports: [
        MatButtonModule,
        MatCheckboxModule,
        MatTabsModule,
        MatToolbarModule,
        MatCardModule,
        NavbarModule,
        WavesModule,
        ButtonsModule,
        MatSidenavModule,
        MatListModule,
        MatGridListModule,
    ],
    exports: [
        MatButtonModule,
        MatCheckboxModule,
        MatTabsModule,
        MatToolbarModule,
        MatCardModule,
        NavbarModule,
        WavesModule,
        ButtonsModule,
        MatSidenavModule,
        MatListModule,
        MatGridListModule,
    ],
})
export class AngularMaterialModule { }
