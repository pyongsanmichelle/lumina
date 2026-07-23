export interface paths {
  '/bff/auth/me': {
    get: {
      responses: {
        200: {
          content: {
            'application/json': {
              authenticated: true;
              userId?: string;
              username?: string;
              roles?: string[];
            };
          };
        };
        401: {
          content: {
            'application/json': {
              authenticated: false;
              message: string;
            };
          };
        };
      };
    };
  };
}
