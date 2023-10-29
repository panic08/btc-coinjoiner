import React from 'react';
import { Link } from 'react-router-dom'
import './Footer.css'
const Footer = () => {
    return (
        <div className="footer">
            <div className="footer-logo"><Link to="/">MarthaJoin</Link></div>
            <div className="footer-nav">
                <div className="footer-links"><Link to="/">FAQs</Link></div>
                <div className="footer-links"><Link to="/">Правила сервиса</Link></div>
                <div className="footer-links"><Link to="/">AML Policy</Link></div>
                <div className="footer-links"><Link to="/">Контакты</Link></div>
            </div>
        </div>
    );
};

export default Footer;