package com.ptesa.efacturacion.feservicesforclients.httpfilters;

import com.ptesa.efacturacion.feservicesforclients.repository.CompanyRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter to validate access to URLs containing /companies/*
 */
@Component
@Order(1)
@AllArgsConstructor
public class CompaniesSecurityFilter implements Filter {

    // Constants

    private static final String COMPANY_PATTERN = "^/companies/(\\d+)/";


    // Fields

    private final CompanyRepository companyRepository;


    // Logic

    /**
     * Validates user roles for requests that are related with companies.
     * @param request Request
     * @param response Response
     * @param chain Filter chain
     * @throws IOException IO Exception.
     * @throws ServletException Servlet Exception.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String requestUri = req.getRequestURI().replace(req.getContextPath(), "");
        String username = this.obtainUsernameFromHeader(req);

        if (this.requiresValidation(requestUri)) {
            // Extract company ID
            Optional<Long> companyIdOptional = this.extractCompany(requestUri);

            // URL does not contain a company ID
            if (companyIdOptional.isEmpty()) {
                chain.doFilter(request, response);
                return;
            }

            // Validate if the user has permissions to the company
            if (this.companyRepository.existsUserInCompany(username, companyIdOptional.get())) {
                chain.doFilter(request, response);
                return;
            }

            // User does not have access to the billing document
            res.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Returns true if a URL requires validations. False instead.
     * @param url URL
     * @return true if a URL requires validations. False instead.
     */
    private boolean requiresValidation(String url) {
        Pattern pattern = Pattern.compile(COMPANY_PATTERN);
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    /**
     * Extract the company from the URL
     * @param url URL.
     * @return The company ID if found. Empty otherwise.
     */
    public Optional<Long> extractCompany(String url) {
        Pattern pattern = Pattern.compile(COMPANY_PATTERN);
        Matcher matcher = pattern.matcher(url);

        String occurrence = null;
        if (matcher.find()) {
            occurrence = matcher.group(1);
            return Optional.of(Long.valueOf(occurrence));
        }

        return Optional.empty();
    }

    /**
     * Gets username from header.
     * @param request Http Servlet Request
     * @return The value of the "username" header.
     */
    public String obtainUsernameFromHeader(ServletRequest request) {
         HttpServletRequest httpServletRequest = (HttpServletRequest) request;
         return httpServletRequest.getHeader("username");
    }


}