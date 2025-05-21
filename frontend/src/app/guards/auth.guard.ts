import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private router: Router) { }

    canActivate(): boolean {
        const token = localStorage.getItem('token');

        if (token) {
            // Token encontrado: puede pasar
            return true;
        } else {
            // No hay token: redirigir al login
            this.router.navigate(['/login']);
            return false;
        }
    }
}
