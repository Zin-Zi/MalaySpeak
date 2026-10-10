import React, { useState, useEffect, useMemo } from 'react';
import {
  Search,
  BarChart3,
  Moon,
  Sun,
  Check,
  Flame,
  RotateCcw,
  Info,
  X,
  BookmarkCheck,
  Award,
  ChevronDown
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { getAll500Phrases, PhraseItem } from './data/phrases500.ts';

const CATEGORIES: Array<{ key: string; nameMm: string; nameEn: string }> = [
  { key: 'ALL', nameMm: 'အားလုံး', nameEn: 'All' },
  { key: 'GREETINGS', nameMm: 'နှုတ်ဆက်ခြင်း', nameEn: 'Greetings' },
  { key: 'WORKPLACE', nameMm: 'လုပ်ငန်းခွင်', nameEn: 'Workplace' },
  { key: 'SHOPPING', nameMm: 'ဈေးဝယ်ခြင်း', nameEn: 'Shopping' },
  { key: 'TRANSPORT', nameMm: 'သွားလာရေး', nameEn: 'Transport' },
  { key: 'EMERGENCY', nameMm: 'အရေးပေါ်', nameEn: 'Emergency' },
  { key: 'FOOD', nameMm: 'အစားအသောက်', nameEn: 'Food' },
];

export default function App() {
  // Theme state: defaults to system theme, persists in local storage
  const [isDark, setIsDark] = useState<boolean>(() => {
    const saved = localStorage.getItem('malayspeak_theme_mode');
    if (saved === 'dark') return true;
    if (saved === 'light') return false;
    // Default: Follow system theme
    if (typeof window !== 'undefined' && window.matchMedia) {
      return window.matchMedia('(prefers-color-scheme: dark)').matches;
    }
    return false;
  });

  useEffect(() => {
    localStorage.setItem('malayspeak_theme_mode', isDark ? 'dark' : 'light');
    if (isDark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [isDark]);

  const [activeTab, setActiveTab] = useState<'translate' | 'progress'>('translate');

  // Persistent 500-phrase deck from Room Database
  const [deck, setDeck] = useState<PhraseItem[]>(() => {
    const saved = localStorage.getItem('malayspeak_m3_deck_500');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        if (Array.isArray(parsed) && parsed.length >= 50) {
          const seen = new Set<number>();
          let hasDuplicate = false;
          for (const item of parsed) {
            if (seen.has(item.id)) {
              hasDuplicate = true;
              break;
            }
            seen.add(item.id);
          }
          if (!hasDuplicate) {
            return parsed;
          }
          return parsed.map((item: PhraseItem, idx: number) => ({
            ...item,
            id: idx + 1,
          }));
        }
      } catch {
        // Fall back
      }
    }
    return getAll500Phrases();
  });

  useEffect(() => {
    localStorage.setItem('malayspeak_m3_deck_500', JSON.stringify(deck));
  }, [deck]);

  // Translate / Search Screen State
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [expandedTranslateIds, setExpandedTranslateIds] = useState<Set<number>>(new Set());

  const toggleExpandTranslate = (id: number) => {
    setExpandedTranslateIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  // Filtered Search Results for Translate Tab
  const searchResults = useMemo(() => {
    let result = deck;
    if (selectedCategory !== 'ALL') {
      result = result.filter((item) => item.category === selectedCategory);
    }
    if (!searchQuery.trim()) {
      return result;
    }
    const q = searchQuery.toLowerCase().trim();
    return result.filter(
      (item) =>
        item.malayPhrase.toLowerCase().includes(q) ||
        item.englishMeaning.toLowerCase().includes(q) ||
        item.myanmarMeaning.includes(q) ||
        item.malayPronunciation.includes(q)
    );
  }, [deck, searchQuery, selectedCategory]);

  const toggleLearnCard = (id: number) => {
    setDeck((prev) => {
      const next = prev.map((c) => (c.id === id ? { ...c, isLearned: !c.isLearned } : c));
      const target = next.find((c) => c.id === id);
      if (target?.isLearned) {
        confetti({
          particleCount: 30,
          spread: 50,
          origin: { y: 0.7 },
          colors: ['#000000', '#737373', '#D4D4D4', '#FFFFFF']
        });
      }
      return next;
    });
  };

  const resetAllProgress = () => {
    if (window.confirm('သင်ယူထားသော စကားစုမှတ်တမ်းအားလုံးကို ပြန်လည်စတင်မှာ သေချာပါသလား။')) {
      setDeck((prev) => prev.map((c) => ({ ...c, isLearned: false })));
    }
  };

  // Metrics
  const totalLearned = deck.filter((c) => c.isLearned).length;
  const progressPercent = Math.round((totalLearned / deck.length) * 100);

  return (
    <div className={`min-h-screen ${isDark ? 'dark bg-[#0A0A0A] text-white' : 'bg-[#FAFAFA] text-[#0A0A0A]'}`}>
      {/* Mobile-First Centered Container */}
      <div className="max-w-md mx-auto min-h-screen flex flex-col bg-white dark:bg-[#0A0A0A] border-x border-neutral-200 dark:border-neutral-800 shadow-sm relative">
        
        {/* Main Content Area: Starts directly with content (No mock status bar, no TopAppBar header) */}
        <main className="flex-1 overflow-y-auto px-5 py-5">
          
          {/* SCREEN 1: TRANSLATE / SEARCH */}
          {activeTab === 'translate' && (
            <div className="space-y-4 pb-6">
              {/* Animated Google AI Glow Border Search Bar with Theme Toggle Button in top-right corner */}
              <div className="flex items-center gap-2">
                <div className="flex-1 google-ai-glow-container shadow-sm">
                  <div className="flex items-center bg-white dark:bg-[#0A0A0A] rounded-[12px] px-3.5 py-2.5">
                    <Search className="w-4 h-4 text-neutral-500 shrink-0 mr-2.5" />
                    <input
                      type="text"
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      placeholder="စကားစု ရှာဖွေပါ (Burmese, English, Malay)..."
                      className="w-full text-sm bg-transparent outline-none placeholder:text-neutral-400 dark:placeholder:text-neutral-500 text-neutral-900 dark:text-white"
                    />
                    {searchQuery && (
                      <button
                        onClick={() => setSearchQuery('')}
                        className="text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-200 p-0.5"
                      >
                        <X className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                </div>

                {/* Minimalist Monochrome Theme Toggle Button (Sun / Moon) */}
                <button
                  onClick={() => setIsDark((prev) => !prev)}
                  title={isDark ? "အလင်းရောင်မုဒ်သို့ ပြောင်းရန် (Switch to Light Mode)" : "အမှောင်မုဒ်သို့ ပြောင်းရန် (Switch to Dark Mode)"}
                  aria-label={isDark ? "Switch to Light Mode" : "Switch to Dark Mode"}
                  className="w-11 h-11 shrink-0 rounded-xl border border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-900 flex items-center justify-center text-neutral-800 dark:text-neutral-200 hover:border-neutral-400 dark:hover:border-neutral-600 transition-colors"
                >
                  {isDark ? (
                    <Sun className="w-4 h-4 text-white" />
                  ) : (
                    <Moon className="w-4 h-4 text-neutral-900" />
                  )}
                </button>
              </div>

              {/* Minimalist Monochrome Category Filter Chips */}
              <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none text-xs">
                {CATEGORIES.map((cat) => {
                  const isSelected = selectedCategory === cat.key;
                  return (
                    <button
                      key={cat.key}
                      onClick={() => setSelectedCategory(cat.key)}
                      className={`px-3 py-1.5 rounded-lg text-xs transition-colors whitespace-nowrap border ${
                        isSelected
                          ? 'bg-black text-white border-black dark:bg-white dark:text-black dark:border-white font-medium'
                          : 'bg-white dark:bg-neutral-900 text-neutral-600 dark:text-neutral-400 border-neutral-200 dark:border-neutral-800 hover:border-neutral-400'
                      }`}
                    >
                      {cat.nameMm}
                    </button>
                  );
                })}
              </div>

              {/* Immediately below Filter Chips: List of Phrases ("စကားစုများ") Header & Main Content */}
              <div className="space-y-3 pt-1">
                <div className="flex items-center justify-between text-xs px-0.5">
                  <span className="text-sm font-bold text-neutral-900 dark:text-white">
                    စကားစုများ
                  </span>
                  <span className="text-neutral-500 font-mono text-xs border border-neutral-200 dark:border-neutral-800 rounded px-2 py-0.5">
                    {searchResults.length} ခု
                  </span>
                </div>

                {searchResults.length === 0 ? (
                  <div className="text-center py-12 text-neutral-400 text-sm">
                    စကားစုများ မတွေ့ရှိပါ
                  </div>
                ) : (
                  <div className="space-y-2.5">
                    {searchResults.map((item) => {
                      const isExpanded = expandedTranslateIds.has(item.id);
                      return (
                        <div
                          key={`translate-card-${item.id}`}
                          className={`bg-white dark:bg-[#121212] rounded-xl border transition-all duration-200 overflow-hidden ${
                            isExpanded
                              ? 'border-neutral-400 dark:border-neutral-600 bg-neutral-50/50 dark:bg-neutral-900/40'
                              : 'border-neutral-200 dark:border-neutral-800 hover:border-neutral-300 dark:hover:border-neutral-700'
                          }`}
                        >
                          {/* Collapsed Header: Shows ONLY the Malay phrase */}
                          <button
                            onClick={() => toggleExpandTranslate(item.id)}
                            className="w-full p-4 flex items-center justify-between text-left focus:outline-none select-none transition-colors"
                          >
                            <div className="flex items-center gap-2.5 pr-2 flex-1">
                              {item.isLearned && (
                                <BookmarkCheck className="w-4 h-4 text-neutral-900 dark:text-white shrink-0" />
                              )}
                              <span className="font-semibold text-sm text-neutral-900 dark:text-white leading-snug">
                                {item.malayPhrase}
                              </span>
                            </div>

                            <div
                              className={`w-6 h-6 rounded-md flex items-center justify-center shrink-0 transition-transform duration-300 ${
                                isExpanded
                                  ? 'rotate-180 bg-neutral-200 dark:bg-neutral-800 text-neutral-900 dark:text-white'
                                  : 'text-neutral-400 bg-neutral-100 dark:bg-neutral-900'
                              }`}
                            >
                              <ChevronDown className="w-3.5 h-3.5" />
                            </div>
                          </button>

                          {/* Smooth Accordion Body (Expanded details in Monochrome) */}
                          <div
                            className={`transition-all duration-300 ease-in-out ${
                              isExpanded ? 'max-h-[600px] opacity-100' : 'max-h-0 opacity-0 pointer-events-none'
                            }`}
                          >
                            <div className="px-4 pb-4 pt-1 space-y-3.5 border-t border-neutral-150 dark:border-neutral-800">
                              {/* Pronunciation */}
                              {item.malayPronunciation && (
                                <div className="text-xs text-neutral-600 dark:text-neutral-400 font-medium pt-1">
                                  အသံထွက်: {item.malayPronunciation}
                                </div>
                              )}

                              {/* Myanmar Translation */}
                              <div>
                                <span className="text-[11px] text-neutral-500 dark:text-neutral-400 font-medium block">
                                  မြန်မာပြန်:
                                </span>
                                <p className="text-sm font-semibold text-neutral-900 dark:text-white mt-0.5 leading-relaxed">
                                  {item.myanmarMeaning}
                                </p>
                              </div>

                              {/* English Translation */}
                              <div>
                                <span className="text-[11px] text-neutral-500 dark:text-neutral-400 font-medium block">
                                  English:
                                </span>
                                <p className="text-xs text-neutral-600 dark:text-neutral-400 mt-0.5 leading-relaxed">
                                  {item.englishMeaning}
                                </p>
                              </div>

                              {/* The Explanation (Why it is translated that way) */}
                              <div className="bg-neutral-50 dark:bg-neutral-900 rounded-lg p-3 border border-neutral-200 dark:border-neutral-800">
                                <span className="text-xs font-semibold text-neutral-900 dark:text-white flex items-center gap-1.5 mb-1">
                                  <Info className="w-3.5 h-3.5 text-neutral-600 dark:text-neutral-400 shrink-0" />
                                  <span>ဘာသာပြန် ရှင်းလင်းချက် (Explanation):</span>
                                </span>
                                <p className="text-xs text-neutral-600 dark:text-neutral-400 leading-relaxed">
                                  {item.culturalContext}
                                </p>
                              </div>

                              {/* Bottom Row: Category & Mark as Learned Button */}
                              <div className="flex items-center justify-between pt-1">
                                <span className="text-[11px] text-neutral-400">
                                  {item.categoryMm} ({item.category})
                                </span>

                                <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    toggleLearnCard(item.id);
                                  }}
                                  className={`text-xs px-3 py-1.5 rounded-md font-medium flex items-center gap-1.5 border transition-colors ${
                                    item.isLearned
                                      ? 'bg-black text-white border-black dark:bg-white dark:text-black dark:border-white font-medium'
                                      : 'bg-white dark:bg-neutral-900 text-neutral-700 dark:text-neutral-300 border-neutral-300 dark:border-neutral-700 hover:border-neutral-400'
                                  }`}
                                >
                                  <Check className="w-3.5 h-3.5" />
                                  <span>{item.isLearned ? 'လေ့လာပြီးပြီ ✓' : 'မှတ်သားရန်'}</span>
                                </button>
                              </div>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>
          )}



          {/* SCREEN 3: PROGRESS TRACKING (MINIMALIST MONOCHROME) */}
          {activeTab === 'progress' && (
            <div className="space-y-4 pb-6">
              {/* Circular Indicator Card (Clean 1px Border, Monochrome SVG) */}
              <div className="bg-white dark:bg-[#121212] rounded-xl p-6 border border-neutral-200 dark:border-neutral-800 flex flex-col items-center text-center">
                <span className="text-xs font-semibold text-neutral-500 uppercase tracking-wider mb-2">
                  ၅၀၀ စကားစု သင်ယူပြီးမြောက်မှု
                </span>

                {/* Minimalist Monochrome Circular Indicator */}
                <div className="relative w-32 h-32 flex items-center justify-center my-3">
                  <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                    <circle
                      cx="50"
                      cy="50"
                      r="40"
                      className="text-neutral-100 dark:text-neutral-850"
                      strokeWidth="7"
                      stroke="currentColor"
                      fill="transparent"
                    />
                    <circle
                      cx="50"
                      cy="50"
                      r="40"
                      className="text-black dark:text-white transition-all duration-700"
                      strokeWidth="7"
                      strokeDasharray={251.2}
                      strokeDashoffset={251.2 - (251.2 * progressPercent) / 100}
                      strokeLinecap="round"
                      stroke="currentColor"
                      fill="transparent"
                    />
                  </svg>
                  <div className="absolute inset-0 flex flex-col items-center justify-center">
                    <span className="text-2xl font-bold text-neutral-900 dark:text-white">
                      {progressPercent}%
                    </span>
                    <span className="text-[10px] text-neutral-500 font-medium">ပြီးမြောက်</span>
                  </div>
                </div>

                <p className="text-sm font-medium text-neutral-700 dark:text-neutral-300 mt-1">
                  {totalLearned} / {deck.length} ခု ကျက်မှတ်ပြီး
                </p>
              </div>

              {/* Minimalist Stats Cards Row */}
              <div className="grid grid-cols-2 gap-3">
                <div className="bg-neutral-50 dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl p-4">
                  <div className="flex items-center gap-1.5 text-neutral-600 dark:text-neutral-400 text-xs font-medium">
                    <Flame className="w-4 h-4 text-neutral-800 dark:text-neutral-200 shrink-0" />
                    <span>ရက်ဆက်လေ့လာမှု</span>
                  </div>
                  <div className="text-xl font-bold text-neutral-900 dark:text-white mt-2">
                    ၃ ရက်
                  </div>
                </div>

                <div className="bg-neutral-50 dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-800 rounded-xl p-4">
                  <div className="flex items-center gap-1.5 text-neutral-600 dark:text-neutral-400 text-xs font-medium">
                    <Award className="w-4 h-4 text-neutral-800 dark:text-neutral-200 shrink-0" />
                    <span>ကျန်ရှိစကားစု</span>
                  </div>
                  <div className="text-xl font-bold text-neutral-900 dark:text-white mt-2">
                    {deck.length - totalLearned} ခု
                  </div>
                </div>
              </div>

              {/* Category Breakdown with Monochrome Linear Bars */}
              <div className="bg-white dark:bg-[#121212] rounded-xl p-4.5 border border-neutral-200 dark:border-neutral-800 space-y-3.5">
                <h3 className="text-xs font-semibold text-neutral-900 dark:text-white">
                  ကဏ္ဍအလိုက် တိုးတက်မှု (Room DB)
                </h3>

                <div className="space-y-3">
                  {CATEGORIES.filter((c) => c.key !== 'ALL').map((c) => {
                    const catCards = deck.filter((item) => item.category === c.key);
                    const catLearned = catCards.filter((item) => item.isLearned).length;
                    const pct = catCards.length ? Math.round((catLearned / catCards.length) * 100) : 0;
                    return (
                      <div key={c.key} className="space-y-1">
                        <div className="flex items-center justify-between text-xs">
                          <span className="font-medium text-neutral-700 dark:text-neutral-300">
                            {c.nameMm}
                          </span>
                          <span className="text-neutral-500 text-[11px] font-mono">
                            {catLearned}/{catCards.length} ({pct}%)
                          </span>
                        </div>
                        <div className="w-full h-1.5 bg-neutral-100 dark:bg-neutral-800 rounded-full overflow-hidden">
                          <div
                            className="h-full bg-black dark:bg-white rounded-full transition-all duration-300"
                            style={{ width: `${pct}%` }}
                          />
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Reset Progress Button */}
              <div className="pt-1 text-center">
                <button
                  onClick={resetAllProgress}
                  className="text-xs text-neutral-500 hover:text-neutral-900 dark:hover:text-white font-medium hover:underline inline-flex items-center gap-1.5 transition-colors"
                >
                  <RotateCcw className="w-3.5 h-3.5" />
                  <span>တိုးတက်မှု အားလုံး အစမှ ပြန်လည်စတင်ရန်</span>
                </button>
              </div>
            </div>
          )}
        </main>

        {/* Minimalist Bottom Navigation Bar with Grayscale Icons (2 Tabs: Translate & Progress) */}
        <nav className="sticky bottom-0 z-40 bg-white/95 dark:bg-[#0A0A0A]/95 backdrop-blur border-t border-neutral-200 dark:border-neutral-800 grid grid-cols-2 h-16 px-6 shrink-0">
          <button
            onClick={() => setActiveTab('translate')}
            className={`flex flex-col items-center justify-center py-1 transition-colors ${
              activeTab === 'translate'
                ? 'text-black dark:text-white font-semibold'
                : 'text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-300'
            }`}
          >
            <div className={`p-1.5 rounded-full transition-colors ${activeTab === 'translate' ? 'bg-neutral-100 dark:bg-neutral-800' : ''}`}>
              <Search className="w-4 h-4" />
            </div>
            <span className="text-[11px] mt-0.5">ဘာသာပြန်</span>
          </button>

          <button
            onClick={() => setActiveTab('progress')}
            className={`flex flex-col items-center justify-center py-1 transition-colors ${
              activeTab === 'progress'
                ? 'text-black dark:text-white font-semibold'
                : 'text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-300'
            }`}
          >
            <div className={`p-1.5 rounded-full transition-colors ${activeTab === 'progress' ? 'bg-neutral-100 dark:bg-neutral-800' : ''}`}>
              <BarChart3 className="w-4 h-4" />
            </div>
            <span className="text-[11px] mt-0.5">တိုးတက်မှု</span>
          </button>
        </nav>
      </div>
    </div>
  );
}
