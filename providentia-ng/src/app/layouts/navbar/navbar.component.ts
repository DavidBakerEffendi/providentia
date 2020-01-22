import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event, NavigationStart, NavigationEnd } from '@angular/router';

@Component({
    selector: 'prv-navbar',
    templateUrl: './navbar.component.html'
})
export class NavbarComponent {

    title = 'Providentia';

    constructor(
        private location: Location,
        private router: Router
    ) {
        this.changeTitleOnRoute(this.router.url);
        router.events.subscribe((event: Event) => {
            // Listen to changes in the router to add the correct title in the NavBar
            if (event instanceof NavigationStart) { this.changeTitleOnRoute(event.url); }
        });
    }

    changeTitleOnRoute(route: string) {
        if (route === '/') {
            this.title = 'Dashboard';
        } else if (route === '/new-job') {
            this.title = 'New Job';
        } else if (route === '/history') {
            this.title = 'History';
        } else if (route === '/classifier') {
            this.title = 'Classifiers';
        } else if (route === '/databases') {
            this.title = 'Databases';
        } else if (route.includes('benchmark')) {
            this.title = 'Benchmark';
        } else {
            this.title = 'Providentia';
        }
    }
}
