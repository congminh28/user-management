package com.usermanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Skip JWT validation for public endpoints
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Check if this is an API endpoint
        if (path.startsWith("/api/")) {
            String jwt = getJwtFromRequest(request);
            
            if (jwt == null) {
                sendErrorResponse(response, "Token không được cung cấp. Vui lòng đăng nhập để lấy token.", 401);
                return;
            }
            
            try {
                String email = tokenProvider.getEmailFromToken(jwt);
                
                if (email == null) {
                    sendErrorResponse(response, "Token không hợp lệ: Không thể lấy thông tin từ token.", 401);
                    return;
                }
                
                if (!tokenProvider.validateToken(jwt, email)) {
                    sendErrorResponse(response, "Token không hợp lệ hoặc đã hết hạn.", 401);
                    return;
                }
                
                // Token is valid, set authentication
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                sendErrorResponse(response, "Token đã hết hạn. Vui lòng đăng nhập lại.", 401);
                return;
            } catch (io.jsonwebtoken.security.SignatureException e) {
                sendErrorResponse(response, "Token không hợp lệ: Chữ ký không đúng.", 401);
                return;
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                sendErrorResponse(response, "Token không hợp lệ: Định dạng token sai.", 401);
                return;
            } catch (Exception e) {
                logger.error("JWT token validation failed", e);
                sendErrorResponse(response, "Lỗi xác thực token: " + e.getMessage(), 401);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/auth/login") || 
               path.equals("/api/auth/register") ||
               path.startsWith("/login") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.equals("/error");
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", message);
        errorResponse.put("status", status);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}

