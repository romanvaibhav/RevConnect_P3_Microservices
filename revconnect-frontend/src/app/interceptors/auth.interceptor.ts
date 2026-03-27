/*

"Every HTTP request after login automatically gets the token attached:"

const token = localStorage.getItem('token');
const clonedRequest = req.clone({
  setHeaders: { Authorization: `Bearer ${token}` }
});
"This is an Angular HTTP Interceptor — it runs on every outgoing request automatically."
*/



import { HttpInterceptorFn } from '@angular/common/http';

// Functional HTTP Interceptor (Angular 15+ style)
// Automatically attaches JWT token to every outgoing HTTP request
export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // Get token from localStorage (stored during login)
  const token = localStorage.getItem('token');

  // If token exists, attach it to request header
  if (token) {

    // Clone the original request (requests are immutable)
    const clonedRequest = req.clone({
      setHeaders: {
        // Add Authorization header in Bearer format
        Authorization: `Bearer ${token}`
      }
    });

    // Pass modified request to next handler
    return next(clonedRequest);
  }

  // If no token, send request as it is
  return next(req);
};

//clone()=>used to create copy of the original request and modify it without affecting the original request.
//This is necessary because HTTP requests in Angular are immutable, meaning they cannot be changed once created. 
// By cloning the request, we can safely add the Authorization header without altering the original request object.


//interceptore:acts as a bridge between the application and the backend API, allowing us to modify outgoing requests (like adding auth tokens) and handle incoming responses (like catching errors) in a centralized way.