import {
    MatButtonModule,
    MatCheckboxModule,
    MatToolbarModule,
    MatTabsModule,
    MatSidenavModule,
    MatListModule,
    MatCardModule,
    MatGridListModule,
    MatProgressBarModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatExpansionModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
} from '@angular/material';
import {
    NavbarModule,
    WavesModule,
    ButtonsModule,
    ChartsModule,
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
        MatProgressBarModule,
        ChartsModule,
        MatIconModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        MatExpansionModule,
        MatTableModule,
        MatProgressSpinnerModule,
        MatPaginatorModule,
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
        MatProgressBarModule,
        ChartsModule,
        MatIconModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        MatExpansionModule,
        MatTableModule,
        MatProgressSpinnerModule,
        MatPaginatorModule,
    ],
})
export class AngularMaterialModule { }
